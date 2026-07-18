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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.ScreenEntryEffect
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.scaffold.ZavGarBaseScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.accentSoft
import com.zavgar.system.designsystem.theme.border
import com.zavgar.system.designsystem.theme.card
import com.zavgar.system.designsystem.theme.danger
import com.zavgar.system.designsystem.theme.dangerContainer
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundDisabled
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.designsystem.theme.onAccent
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_title_setting
import com.zavgar.system.resources.settings_logout_button
import com.zavgar.system.resources.settings_profil_details_button
import com.zavgar.system.settings.presentation.SettingsEvent
import com.zavgar.system.settings.presentation.SettingsIntent
import com.zavgar.system.settings.presentation.SettingsState
import com.zavgar.system.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToProfileDetail = onNavigateToProfileDetail,
        modifier = modifier,
    )
}

@Composable
internal fun SettingsLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ScreenEntryEffect(viewModel) {
        viewModel.handleIntent(SettingsIntent.ScreenEntered)
    }

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
        modifier = modifier,
    )
}

@Composable
internal fun SettingsScaffold(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    ZavGarBaseScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
    ) { paddingValues ->
        SettingsContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
internal fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    PullToRefreshBox(
        isRefreshing = state.profileState is SettingsState.ProfileState.Loading,
        onRefresh = { onIntent(SettingsIntent.Retry) },
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
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

            AnimatedState(
                targetState = state.profileState,
                contentKey = { it::class },
            ) { profileState ->
                when (profileState) {
                    is SettingsState.ProfileState.Content -> ProfileCard(
                        name = profileState.name,
                        phone = profileState.phone,
                        balance = profileState.balance,
                    )

                    is SettingsState.ProfileState.Loading -> ProfileCardSkeleton()

                    is SettingsState.ProfileState.Error -> ProfileCardError(
                        onRetry = { onIntent(SettingsIntent.Retry) },
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingRow(
                    icon = Icons.Default.Person,
                    title = stringResource(Res.string.settings_profil_details_button),
                    onClick = { onIntent(SettingsIntent.ToProfileDetail) },
                )
                DarkModeRow(
                    checked = state.isDarkTheme,
                    onCheckedChange = { onIntent(SettingsIntent.ToggleDarkMode(it)) },
                )
                SettingRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = stringResource(Res.string.settings_logout_button),
                    onClick = { onIntent(SettingsIntent.Logout) },
                    destructive = true,
                    showChevron = false,
                    isLoading = state.isLoggingOut,
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(name: String, phone: String, balance: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.profileCardModifier(),
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatPhoneNomer(phone),
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
private fun ProfileCardSkeleton() {
    Row(
        modifier = Modifier.profileCardModifier(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shimmerAnimation(CircleShape),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(16.dp)
                    .shimmerAnimation(RoundedCornerShape(6.dp)),
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(13.dp)
                    .shimmerAnimation(RoundedCornerShape(6.dp)),
            )
        }
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(26.dp)
                .shimmerAnimation(RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
private fun ProfileCardError(onRetry: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .profileCardModifier()
            .clickable(role = Role.Button, onClick = onRetry),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.dangerContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = colors.danger,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Не удалось загрузить профиль",
                color = colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Нажмите, чтобы повторить",
                color = colors.foregroundSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Modifier.profileCardModifier(): Modifier {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    return this
        .fillMaxWidth()
        .shadow(elevation = 6.dp, shape = shape, clip = false)
        .clip(shape)
        .background(colors.card)
        .padding(horizontal = 18.dp, vertical = 16.dp)
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
    showChevron: Boolean = true,
    isLoading: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
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
            .clickable(role = Role.Button, enabled = !isLoading, onClick = onClick)
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
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = tint,
                strokeWidth = 2.dp,
            )
        } else if (showChevron) {
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
    val colors = MaterialTheme.colorScheme
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

private const val RUSSIAN_PHONE_DIGITS = 10

private fun formatPhoneNomer(phone: String): String {
    val digits = phone.filter { it.isDigit() }.drop(1)
    return if (digits.length == RUSSIAN_PHONE_DIGITS) {
        digits.replace(
            regex = Regex("(\\d{3})(\\d{3})(\\d{2})(\\d{2})"),
            replacement = "+7 ($1) $2 $3-$4",
        )
    } else {
        phone
    }
}

@Preview
@Composable
private fun SettingsScaffoldPreview() {
    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = SettingsState(
                    profileState = SettingsState.ProfileState.Content(
                        name = "Михаил Иванов",
                        phone = "+7 (999) 123-45-67",
                        balance = 1500,
                    ),
                ),
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
                state = SettingsState(profileState = SettingsState.ProfileState.Loading),
                onIntent = { },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScaffoldErrorPreview() {
    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = SettingsState(profileState = SettingsState.ProfileState.Error),
                onIntent = { },
                snackbarHostState = remember { SnackbarHostState() },
            )
        }
    }
}
