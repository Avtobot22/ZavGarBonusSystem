package com.zavgar.system.core.presentation.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(
    private val mask: String,
    private val maskChar: Char = '#'
) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != maskChar }

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        var textIndex = 0

        while (textIndex < text.text.length && maskIndex < mask.length) {
            if (mask[maskIndex] == maskChar) {
                out += text.text[textIndex]
                textIndex++
                maskIndex++
            } else {
                out += mask[maskIndex]
                maskIndex++
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }

    private val offsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            var offsetTotal = 0
            var textIndex = 0

            for (i in mask.indices) {
                if (textIndex >= offset) break

                if (mask[i] == maskChar) {
                    textIndex++
                }
                offsetTotal++
            }
            return offsetTotal
        }

        override fun transformedToOriginal(offset: Int): Int {
            var textIndex = 0

            for (i in 0 until offset) {
                if (i < mask.length && mask[i] == maskChar) {
                    textIndex++
                }
            }
            return textIndex
        }
    }
}