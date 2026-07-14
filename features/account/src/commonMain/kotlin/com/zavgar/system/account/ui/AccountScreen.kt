package com.zavgar.system.account.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.account.presentation.AccountEvent
import com.zavgar.system.account.presentation.AccountIntent
import com.zavgar.system.account.presentation.AccountState
import com.zavgar.system.account.presentation.AccountViewModel
import com.zavgar.system.account.ui.components.DeleteAccountDialog
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.ScreenEntryEffect
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.button.ZavGarBackButton
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.scaffold.ZavGarBaseScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppDatePickerField
import com.zavgar.system.designsystem.components.textfield.AppTextField
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.designsystem.screen.ErrorScreen
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.ZavGarTopSheetShape
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.card
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_confirm
import com.zavgar.system.resources.account_personal_data
import com.zavgar.system.resources.account_profile_confirmed
import com.zavgar.system.resources.account_top_title
import com.zavgar.system.resources.birth_date_label
import com.zavgar.system.resources.birth_date_placeholder
import com.zavgar.system.resources.register_name_label
import com.zavgar.system.resources.register_name_placeholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

private const val DELETE_SUCCESS_VISIBLE_MILLIS = 1500L

@Composable
fun AccountScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
internal fun AccountLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ScreenEntryEffect(viewModel) {
        viewModel.handleIntent(AccountIntent.ScreenEntered)
    }

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is AccountEvent.NavigateBack -> onNavigateBack()
            is AccountEvent.DeleteAccountSuccess -> {
                scope.launch {
                    val shown = launch {
                        snackbarHostState.showCustomSnackbar(
                            type = event.message.type,
                            message = event.message.message.suspendAsString(),
                            withDismissAction = true,
                        )
                    }
                    delay(DELETE_SUCCESS_VISIBLE_MILLIS.milliseconds)
                    shown.cancel()
                    onNavigateToLogin()
                }
            }

            is AccountEvent.ShowSnackbar -> {
                scope.launch { errorShakingState.shake() }
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

    AccountScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier,
    )
}

@Composable
internal fun AccountScaffold(
    state: AccountState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AccountIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier,
) {
    ZavGarBaseScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        applyStatusBarsPadding = false,
    ) { paddingValues ->
        AnimatedState(targetState = state, contentKey = { it.screenState::class }) { state ->
            when (state.screenState) {
                AccountState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(AccountIntent.Retry) },
                    modifier = Modifier.statusBarsPadding().padding(paddingValues),
                    shakingState = errorShakingState,
                )

                AccountState.ScreenState.Initial,
                AccountState.ScreenState.Loading,
                    -> AccountLoading(
                    onBack = { onIntent(AccountIntent.ClickBack) },
                )

                AccountState.ScreenState.Content,
                AccountState.ScreenState.Submitting,
                    -> AccountContent(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
internal fun AccountContent(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme

    DeleteAccountDialog(state = state, onIntent = onIntent)
    AppDatePicker(
        initialDate = state.birthDate,
        isOpen = state.isDatePickerOpen,
        onDismiss = { onIntent(AccountIntent.DismissDatePicker) },
        onConfirm = { onIntent(AccountIntent.EnterBirthDate(it)) },
    )

    AccountScrollContainer(
        modifier = modifier,
        hero = {
            AccountHeroSection(
                name = state.name,
                onBack = { onIntent(AccountIntent.ClickBack) },
                onDelete = { onIntent(AccountIntent.ClickDelete) },
            )
        },
    ) {
        Text(
            text = stringResource(Res.string.account_personal_data),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.foreground,
        )

        AppTextField(
            value = state.name,
            onValueChange = { onIntent(AccountIntent.EnterName(it)) },
            label = stringResource(Res.string.register_name_label),
            placeholder = stringResource(Res.string.register_name_placeholder),
            isError = state.nameError != null,
            errorMessage = state.nameError?.asString(),
            enabled = !state.isSubmitting,
        )

        AppDatePickerField(
            value = state.birthDateText,
            onClick = { onIntent(AccountIntent.OpenDatePicker) },
            label = stringResource(Res.string.birth_date_label),
            placeholder = stringResource(Res.string.birth_date_placeholder),
            isError = state.birthDateError != null,
            errorMessage = state.birthDateError?.asString(),
            enabled = !state.isSubmitting,
        )

        Spacer(Modifier.height(8.dp))

        AppPrimaryButton(
            text = stringResource(Res.string.account_confirm),
            onClick = {
                focusManager.clearFocus()
                onIntent(AccountIntent.Submit)
            },
            enabled = !state.isSubmitting,
            isLoading = state.isSubmitting,
        )
    }
}

@Composable
private fun HeroContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val accent = MaterialTheme.colorScheme.accent
    val heroGradient = Brush.linearGradient(
        colors = listOf(accent, lerp(accent, Color.White, 0.12f)),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
    )
    val blobColor = Color.White.copy(alpha = 0.10f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(heroGradient)
            .statusBarsPadding()
            .padding(bottom = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .background(blobColor, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .background(blobColor, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = 30.dp)
                .background(blobColor, CircleShape),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

private val SheetOverlap = 20.dp

/**
 * Скроллируемый каркас экрана: оранжевая шапка [hero] и карточка контента под ней.
 *
 * Скроллится экран целиком — при открытии клавиатуры шапка уезжает вверх.
 * Карточка приподнята на [SheetOverlap], чтобы её скруглённые углы перекрыли
 * шапку, и растянута минимум до низа экрана (без зазора под ней).
 */
@Composable
private fun AccountScrollContainer(
    hero: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val overlapPx = with(LocalDensity.current) { SheetOverlap.roundToPx() }
    var heroHeightPx by remember { mutableStateOf(0) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val viewportPx = constraints.maxHeight
        val sheetMinHeightPx = (viewportPx - heroHeightPx + overlapPx).coerceAtLeast(0)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            Box(modifier = Modifier.onSizeChanged { heroHeightPx = it.height }) {
                hero()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            constraints.copy(
                                minHeight = sheetMinHeightPx.coerceAtLeast(constraints.minHeight),
                            ),
                        )
                        layout(
                            placeable.width,
                            (placeable.height - overlapPx).coerceAtLeast(0),
                        ) {
                            placeable.place(0, -overlapPx)
                        }
                    }
                    .clip(ZavGarTopSheetShape)
                    .background(colors.card),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = sheetContent,
                )
            }
        }
    }
}

@Composable
private fun AccountHeroSection(
    name: String,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeroContainer(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GlassBackButton(onClick = onBack)
            Text(
                text = stringResource(Res.string.account_top_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            HeroDeleteButton(onClick = onDelete)
        }

        Spacer(Modifier.height(12.dp))

        val firstLetter = name.firstOrNull()?.uppercase() ?: ""
        DashedAvatarRing {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = firstLetter,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.accent,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(10.dp))

        ProfileConfirmedBadge()

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DashedAvatarRing(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(124.dp)
            .drawBehind {
                val strokeWidthPx = 2.dp.toPx()
                val dashWidthPx = 8.dp.toPx()
                val gapWidthPx = 6.dp.toPx()
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = size.minDimension / 2f - strokeWidthPx / 2f,
                    style = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashWidthPx, gapWidthPx),
                            0f,
                        ),
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun GlassBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ZavGarBackButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun HeroDeleteButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Удалить аккаунт",
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ProfileConfirmedBadge() {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(Res.string.account_profile_confirmed),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun AccountLoading(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val placeholderColor = Color.White.copy(alpha = 0.25f)

    AccountScrollContainer(
        modifier = modifier,
        hero = {
            HeroContainer {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    GlassBackButton(onClick = onBack)
                    Text(
                        text = stringResource(Res.string.account_top_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.18f),
                                RoundedCornerShape(12.dp),
                            ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                DashedAvatarRing {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .background(placeholderColor, CircleShape),
                    )
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(20.dp)
                        .background(placeholderColor, RoundedCornerShape(8.dp)),
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(26.dp)
                        .background(placeholderColor, RoundedCornerShape(20.dp)),
                )

                Spacer(Modifier.height(8.dp))
            }
        },
    ) {
        Text(
            text = stringResource(Res.string.account_personal_data),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.foreground,
        )

        AccountFieldSkeleton()
        AccountFieldSkeleton()

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shimmerAnimation(RoundedCornerShape(16.dp)),
        )
    }
}

@Composable
private fun AccountFieldSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(13.dp)
                .shimmerAnimation(RoundedCornerShape(6.dp)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shimmerAnimation(RoundedCornerShape(14.dp)),
        )
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    val mockState = AccountState(
        screenState = AccountState.ScreenState.Content,
        name = "Иван Петров",
        phone = "9991234567",
        birthDate = LocalDate(1990, 5, 15),
        birthDateText = "15.05.1990",
        nameError = null,
        birthDateError = null,
        isDatePickerOpen = false,
    )
    ZavGarThemePreview {
        Screen {
            AccountScaffold(
                state = mockState,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}

@Preview
@Composable
fun AccountScreenWithErrorsPreview() {
    val mockState = AccountState(
        screenState = AccountState.ScreenState.Content,
        name = "Иван",
        phone = "9991234567",
        birthDate = null,
        birthDateText = "",
        nameError = UiText.Resource(Res.string.register_name_placeholder),
        birthDateError = UiText.Resource(Res.string.birth_date_placeholder),
        isDatePickerOpen = false,
    )
    ZavGarThemePreview {
        Screen {
            AccountScaffold(
                state = mockState,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}
