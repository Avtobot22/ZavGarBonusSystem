package com.zavgar.system.navigationapi.transition

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * Навигационные переходы между экранами (Navigation3 [androidx.navigation3.ui.NavDisplay]).
 *
 * Каждый переход — это [ContentTransform] для metadata записи навграфа:
 *  - `transitionSpec`    — переход вперёд (навигация НА экран);
 *  - `popTransitionSpec` — возврат назад (навигация С экрана).
 *
 * Сгруппированы по флоу: [AuthEnterTransition] / [AuthPopTransition] для
 * экранов авторизации, [MainEnterTransition] / [MainPopTransition] — для
 * основного флоу приложения.
 */

// --- Длительности (мс) ---
private const val AUTH_FADE_IN = 150
private const val AUTH_FADE_OUT = 100
private const val MAIN_DURATION = 150
private const val BOTTOM_BAR_DURATION = 220

/* ------------------------------ AUTH FLOW ------------------------------ */
/*
 * Чистый кросс-фейд: экран просто проявляется, ничего не двигается —
 * поэтому на быстрых переключениях нет «подпрыгивания».
 */

/** Вперёд: новый экран проявляется, текущий гаснет. */
val AuthEnterTransition: ContentTransform =
    fadeIn(tween(AUTH_FADE_IN, easing = FastOutSlowInEasing)) togetherWith
            fadeOut(tween(AUTH_FADE_OUT, easing = FastOutSlowInEasing))

/** Назад: симметричный кросс-фейд. */
val AuthPopTransition: ContentTransform = AuthEnterTransition

/* ------------------------------ MAIN FLOW ------------------------------ */
/*
 * Простой горизонтальный сдвиг: новый экран въезжает сбоку, старый уезжает.
 * Без fade — ничего не «рябит» при переключении.
 */

/** Вперёд: новый экран въезжает справа, старый уезжает влево. */
val MainEnterTransition: ContentTransform =
    fadeIn(
        animationSpec = tween(MAIN_DURATION, easing = FastOutSlowInEasing)
    ) togetherWith fadeOut(
        animationSpec = tween(MAIN_DURATION, easing = FastOutSlowInEasing)
    )

/** Назад: предыдущий экран въезжает слева, текущий уезжает вправо. */
val MainPopTransition: ContentTransform =
    fadeIn(
        animationSpec = tween(MAIN_DURATION, easing = FastOutSlowInEasing)
    ) togetherWith fadeOut(
        animationSpec = tween(MAIN_DURATION, easing = FastOutSlowInEasing)
    )

/* ------------------------------ BOTTOM BAR ----------------------------- */

/** Нижняя навигация выезжает снизу. */
val BottomBarEnterTransition: EnterTransition =
    slideInVertically(
        animationSpec = tween(BOTTOM_BAR_DURATION, easing = FastOutSlowInEasing),
        initialOffsetY = { fullHeight -> fullHeight },
    ) + fadeIn(
        animationSpec = tween(BOTTOM_BAR_DURATION, easing = FastOutSlowInEasing),
    )

/** Нижняя навигация уезжает вниз. */
val BottomBarExitTransition: ExitTransition =
    slideOutVertically(
        animationSpec = tween(BOTTOM_BAR_DURATION, easing = FastOutSlowInEasing),
        targetOffsetY = { fullHeight -> fullHeight },
    ) + fadeOut(
        animationSpec = tween(BOTTOM_BAR_DURATION, easing = FastOutSlowInEasing),
    )
