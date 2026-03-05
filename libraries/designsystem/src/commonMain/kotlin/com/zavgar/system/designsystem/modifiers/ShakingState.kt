package com.zavgar.system.designsystem.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class ShackingState(
    private val power: ShakePower,
    private val direction: ShackingDirections,
) {

    val xPosition = Animatable(0f)

    suspend fun shake(duration: Int = 30) {
        val shakeAnimationSpec: AnimationSpec<Float> = tween(duration)

        when (direction) {
            ShackingDirections.LEFT_THEN_RIGHT -> shakeToLeftThenRight(shakeAnimationSpec)
        }
    }

    private suspend fun shakeToLeftThenRight(shakeAnimationSpec: AnimationSpec<Float>) {
        repeat(times = 3) {
            xPosition.animateTo(-power.value, shakeAnimationSpec)
            xPosition.animateTo(0f, shakeAnimationSpec)
            xPosition.animateTo(power.value / 2, shakeAnimationSpec)
            xPosition.animateTo(0f, shakeAnimationSpec)
        }
    }

    sealed class ShakePower(val value: Float) {

        data object Low : ShakePower(value = 30f)

        data object Medium : ShakePower(value = 45f)

        data object High : ShakePower(value = 60f)

        data class Custom(val power: Float) : ShakePower(power)
    }

    enum class ShackingDirections {
        LEFT_THEN_RIGHT,
    }
}

@Composable
fun rememberShackingState(
    power: ShackingState.ShakePower = ShackingState.ShakePower.Medium,
    direction: ShackingState.ShackingDirections = ShackingState.ShackingDirections.LEFT_THEN_RIGHT,
): ShackingState {
    return remember {
        ShackingState(
            power,
            direction
        )
    }
}