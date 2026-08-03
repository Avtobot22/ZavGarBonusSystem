package com.zavgar.system.confirmation.ui.components

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

@Composable
internal actual fun PlatformOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    enabled: Boolean,
    autoFocus: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = { content() },
        cursorBrush = SolidColor(Color.Transparent),
        modifier = modifier
            .focusRequester(focusRequester)
            .semantics {
                contentType = ContentType.SmsOtpCode
            },
    )
}

@Composable
internal actual fun rememberPlatformOtpAutofillController(
    codeLength: Int,
    onCodeReceived: (String) -> Unit,
): OtpAutofillController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCodeReceived = rememberUpdatedState(onCodeReceived)
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data
                ?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                ?.let { extractOtpCode(it, codeLength) }
                ?.let { currentOnCodeReceived.value(it) }
        }
    }
    val controller = remember { AndroidOtpAutofillController() }

    DisposableEffect(context, lifecycleOwner, codeLength) {
        var isRegistered = false
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, intent: Intent) {
                if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return

                val extras = intent.extras ?: return
                when (extras.status()?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = extras.consentIntent() ?: return
                        try {
                            resultLauncher.launch(consentIntent)
                        } catch (_: RuntimeException) {
                            // Manual input remains available.
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> Unit
                }
            }
        }

        fun unregisterReceiver() {
            if (!isRegistered) return
            runCatching { context.unregisterReceiver(receiver) }
            isRegistered = false
        }

        fun registerReceiverIfNeeded() {
            if (isRegistered) return
            try {
                context.registerSmsConsentReceiver(receiver)
                isRegistered = true
            } catch (_: RuntimeException) {
                unregisterReceiver()
            }
        }

        fun startListening() {
            if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
            registerReceiverIfNeeded()
            if (!isRegistered) return
            try {
                SmsRetriever.getClient(context)
                    .startSmsUserConsent(null)
                    .addOnFailureListener {
                        // Google Play services are optional; keep manual input unchanged.
                    }
            } catch (_: RuntimeException) {
                // Google Play services are optional; keep manual input unchanged.
            }
        }

        controller.startAction = ::startListening
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controller.start()
                Lifecycle.Event.ON_STOP,
                Lifecycle.Event.ON_DESTROY,
                -> unregisterReceiver()

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.startAction = {}
            unregisterReceiver()
        }
    }

    return controller
}

private class AndroidOtpAutofillController : OtpAutofillController {
    var startAction: () -> Unit = {}

    override fun start() = startAction()
}

private fun Context.registerSmsConsentReceiver(receiver: BroadcastReceiver) {
    val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(
            receiver,
            filter,
            SmsRetriever.SEND_PERMISSION,
            null,
            Context.RECEIVER_EXPORTED,
        )
    } else {
        @Suppress("DEPRECATION")
        registerReceiver(receiver, filter, SmsRetriever.SEND_PERMISSION, null)
    }
}

private fun Bundle.status(): Status? =
    parcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)

private fun Bundle.consentIntent(): Intent? =
    parcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)

private fun <T : android.os.Parcelable> Bundle.parcelable(key: String, type: Class<T>): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, type)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
