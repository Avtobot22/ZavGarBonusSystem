package com.zavgar.system.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.scaffold.ZavGarBaseScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.LocalZavGarColors
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_title_setting
import com.zavgar.system.resources.settings_about_app_button
import com.zavgar.system.resources.settings_logout_button
import com.zavgar.system.resources.settings_profil_details_button
import com.zavgar.system.settings.presentation.SettingsEvent
import com.zavgar.system.settings.presentation.SettingsIntent
import com.zavgar.system.settings.presentation.SettingsState
import com.zavgar.system.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToProfileDetail = onNavigateToProfileDetail,
        modifier = modifier
    )
}

@Composable
internal fun SettingsLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is SettingsEvent.NavigateToProfileDetail -> onNavigateToProfileDetail()
            is SettingsEvent.NavigateToLogin -> onNavigateToLogin()
            is SettingsEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showCustomSnackbar(
                        type = event.message.type,
                        message = event.message.message.suspendAsString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    SettingsScaffold(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
internal fun SettingsScaffold(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    ZavGarBaseScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
    ) { paddingValues ->
        when (state.screenState) {
            is SettingsState.ScreenState.Content -> SettingsContent(
                onIntent = onIntent,
                modifier = Modifier.padding(paddingValues)
            )

            is SettingsState.ScreenState.Loading -> SettingsLoading(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
internal fun SettingsContent(
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalZavGarColors.current
    val scrollState = rememberScrollState()
    var darkMode by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp, bottom = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_title_setting),
            color = colors.foreground,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        )

        ProfileCard(name = "Михаил Иванов", phone = "+7 (999) 123-45-67", balance = 1500)

        Spacer(Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SettingRow(
                icon = Icons.Default.Person,
                title = stringResource(Res.string.settings_profil_details_button),
                onClick = { onIntent(SettingsIntent.ToProfileDetail) },
            )
            SettingRow(
                icon = Icons.Default.Info,
                title = stringResource(Res.string.settings_about_app_button),
                onClick = { /* TODO: about screen */ },
            )
            DarkModeRow(
                checked = darkMode,
                onCheckedChange = { darkMode = it },
            )
            SettingRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = stringResource(Res.string.settings_logout_button),
                onClick = { onIntent(SettingsIntent.Logout) },
                destructive = true,
                showChevron = false,
            )
        }
    }
}

@Composable
private fun ProfileCard(name: String, phone: String, balance: Int) {
    val colors = LocalZavGarColors.current
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = colors.onAccent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = colors.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = phone,
                color = colors.foregroundSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.accentSoft)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = "$balance б",
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
    showChevron: Boolean = true,
) {
    val colors = LocalZavGarColors.current
    val shape = RoundedCornerShape(18.dp)
    val tint = if (destructive) colors.danger else colors.accent
    val iconBg = if (destructive) colors.dangerContainer else colors.accentSoft
    val titleColor = if (destructive) colors.danger else colors.foreground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            color = titleColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.foregroundDisabled,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun DarkModeRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = LocalZavGarColors.current
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card)
            .clickable(role = Role.Switch) { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = "Тёмная тема",
            color = colors.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onAccent,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.card,
                uncheckedTrackColor = colors.border,
                uncheckedBorderColor = colors.border,
            ),
        )
    }
}

@Composable
internal fun SettingsLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SettingsScaffoldPreview() {
    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = SettingsState(),
                onIntent = { },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScaffoldLoadingPreview() {
    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = SettingsState(screenState = SettingsState.ScreenState.Loading),
                onIntent = { },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
    }
}
