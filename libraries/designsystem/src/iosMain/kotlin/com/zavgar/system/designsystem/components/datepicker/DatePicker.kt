package com.zavgar.system.designsystem.components.datepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.dialog_picker_cancel
import com.zavgar.system.resources.dialog_picker_confirm
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDate
import platform.Foundation.NSSelectorFromString
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UIBarButtonItemStyle
import platform.UIKit.UIBarButtonSystemItem
import platform.UIKit.UIColor
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UIPresentationController
import platform.UIKit.UISheetPresentationControllerDelegateProtocol
import platform.UIKit.UISheetPresentationControllerDetent
import platform.UIKit.UIToolbar
import platform.UIKit.UIUserInterfaceStyle.UIUserInterfaceStyleDark
import platform.UIKit.UIViewController
import platform.UIKit.sheetPresentationController
import platform.darwin.NSObject
import kotlin.time.Instant

@Composable
actual fun AppDatePicker(
    initialDate: LocalDate?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    val timeZone = remember { TimeZone.currentSystemDefault() }
    val cancelTitle = stringResource(Res.string.dialog_picker_cancel)
    val confirmTitle = stringResource(Res.string.dialog_picker_confirm)

    val onDismissState = rememberUpdatedState(onDismiss)
    val onConfirmState = rememberUpdatedState(onConfirm)

    val controllerHolder = remember { mutableStateOf<UIViewController?>(null) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            if (controllerHolder.value == null) {
                val controller = DatePickerViewController(
                    initialDate = initialDate,
                    zone = timeZone,
                    cancelTitle = cancelTitle,
                    confirmTitle = confirmTitle,
                    onConfirm = { date ->
                        onConfirmState.value(date)
                        onDismissState.value()
                    },
                    onCancel = { onDismissState.value() },
                )
                controllerHolder.value = controller
                topViewController()?.presentViewController(controller, animated = true, completion = null)
            }
        } else {
            controllerHolder.value?.dismissViewControllerAnimated(flag = true, completion = null)
            controllerHolder.value = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controllerHolder.value?.dismissViewControllerAnimated(flag = false, completion = null)
            controllerHolder.value = null
        }
    }
}

@OptIn(BetaInteropApi::class)
private class ClosureTarget(private val onTap: () -> Unit) : NSObject() {
    @ObjCAction
    fun invoke() = onTap()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class DatePickerViewController(
    initialDate: LocalDate?,
    private val zone: TimeZone,
    cancelTitle: String,
    confirmTitle: String,
    private val onConfirm: (LocalDate) -> Unit,
    private val onCancel: () -> Unit,
) : UIViewController(nibName = null, bundle = null), UISheetPresentationControllerDelegateProtocol {

    private val picker = UIDatePicker().apply {
        datePickerMode = UIDatePickerMode.UIDatePickerModeDate
        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
        maximumDate = NSDate()
        initialDate?.let {
            val seconds = it.atStartOfDayIn(zone).epochSeconds
            date = NSDate.dateWithTimeIntervalSince1970(seconds.toDouble())
        }
    }

    private val cancelTarget = ClosureTarget { onCancel() }
    private val confirmTarget = ClosureTarget {
        val seconds = picker.date.timeIntervalSince1970
        val selected = Instant.fromEpochSeconds(seconds.toLong())
            .toLocalDateTime(zone)
            .date
        onConfirm(selected)
    }

    private val toolbar = UIToolbar().apply {
        val cancelItem = UIBarButtonItem(
            title = cancelTitle,
            style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
            target = cancelTarget,
            action = NSSelectorFromString("invoke"),
        )
        val flexibleSpace = UIBarButtonItem(
            barButtonSystemItem = UIBarButtonSystemItem.UIBarButtonSystemItemFlexibleSpace,
            target = null,
            action = null,
        )
        val confirmItem = UIBarButtonItem(
            title = confirmTitle,
            style = UIBarButtonItemStyle.UIBarButtonItemStyleDone,
            target = confirmTarget,
            action = NSSelectorFromString("invoke"),
        )
        setItems(listOf(cancelItem, flexibleSpace, confirmItem), animated = false)
    }

    init {
        modalPresentationStyle = UIModalPresentationPageSheet
        sheetPresentationController?.apply {
            val contentDetent = UISheetPresentationControllerDetent.customDetentWithIdentifier(
                identifier = "datePickerContent",
                resolver = { contentHeight() },
            )
            detents = listOf(contentDetent)
            prefersGrabberVisible = true
            delegate = this@DatePickerViewController
        }
    }

    // Высота листа = отступ сверху + кнопки + пикер + нижняя safe area.
    // Размеры берём из самих вьюх, а не хардкодим.
    private fun contentHeight(): Double {
        val bottomInset = view.safeAreaInsets.useContents { bottom }
        val toolbarHeight = toolbar.intrinsicContentSize.useContents { height }
        val pickerHeight = picker.intrinsicContentSize.useContents { height }
        return VERTICAL_PADDING + toolbarHeight + pickerHeight + bottomInset
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        // systemBackground недоступен в биндингах; повторяем его значения вручную.
        view.backgroundColor = if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
            UIColor.blackColor()
        } else {
            UIColor.whiteColor()
        }
        view.addSubview(toolbar)
        view.addSubview(picker)
    }

    private var detentsRecalculated = false

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        val width = view.bounds.useContents { size.width }
        val insets = view.safeAreaInsets.useContents { top to bottom }
        val toolbarHeight = toolbar.intrinsicContentSize.useContents { height }
        val pickerHeight = picker.intrinsicContentSize.useContents { height }
        // Кнопки (тулбар) сверху с отступом, пикер — под ними.
        val toolbarTop = insets.first + VERTICAL_PADDING
        toolbar.setFrame(CGRectMake(0.0, toolbarTop, width, toolbarHeight))
        picker.setFrame(CGRectMake(0.0, toolbarTop + toolbarHeight, width, pickerHeight))

        // Низ листа (home indicator) известен не сразу — пересчитаем detent один раз.
        if (!detentsRecalculated && insets.second > 0.0) {
            detentsRecalculated = true
            sheetPresentationController?.invalidateDetents()
        }
    }

    // Свайп вниз / тап по затемнению — трактуем как отмену.
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onCancel()
    }
}

// Единственный осмысленный design-отступ: зазор над кнопками, чтобы не липли к граберу.
private const val VERTICAL_PADDING = 16.0

private fun topViewController(): UIViewController? {
    var top = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }
    return top
}
