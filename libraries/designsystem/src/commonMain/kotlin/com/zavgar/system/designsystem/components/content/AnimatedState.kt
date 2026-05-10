package com.zavgar.system.designsystem.components.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zavgar.system.designsystem.animation.FadeTransition

@Composable
fun <S : Any> AnimatedState(
    targetState: S,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<S>.() -> ContentTransform = { FadeTransition },
    label: String = "AnimatedState",
    skipAnimation: (S) -> Boolean = { false },
    contentKey: (S) -> Any? = { it },
    content: @Composable (S) -> Unit
) {
    if (skipAnimation(targetState)) {
        content(targetState)
    } else {
        AnimatedContent(
            targetState = targetState,
            modifier = modifier,
            label = label,
            transitionSpec = transitionSpec,
            contentKey = contentKey
        ) { state ->
            content(state)
        }
    }
}