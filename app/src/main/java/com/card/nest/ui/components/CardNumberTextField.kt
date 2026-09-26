package com.card.nest.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Stable
class CardNumberFormatter {
    companion object {
        private const val MAX_DIGITS = 19
        private const val GROUP_SIZE = 4
        
        fun format(input: String): String {
            val digits = input.filter { it.isDigit() }.take(MAX_DIGITS)
            return digits.chunked(GROUP_SIZE).joinToString(" ")
        }
        
        fun normalize(formatted: String): String {
            return formatted.filter { it.isDigit() }
        }
        
        fun calculateCursorPosition(formatted: String, cursorPosition: Int): Int {
            val beforeCursor = formatted.substring(0, cursorPosition)
            val digitsBeforeCursor = beforeCursor.filter { it.isDigit() }.length
            val totalDigits = formatted.filter { it.isDigit() }.length
            
            if (digitsBeforeCursor == 0) return 0
            if (digitsBeforeCursor >= totalDigits) return formatted.length
            
            var result = 0
            var digitCount = 0
            for (char in formatted) {
                if (char.isDigit()) {
                    digitCount++
                    if (digitCount == digitsBeforeCursor) {
                        result++
                        break
                    }
                }
                result++
            }
            
            return result
        }
    }
}

@Composable
fun CardNumberTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val formatter = remember { CardNumberFormatter }
    
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val newText = newValue.text
            val oldText = value.text
            val oldSelection = value.selection
            
            // Calculate normalized cursor position
            val oldDigitsBeforeCursor = oldText.substring(0, oldSelection.start).filter { it.isDigit() }.length
            
            // Format the new text
            val formatted = formatter.format(newText)
            
            // Calculate new cursor position
            val newCursorPosition = formatter.calculateCursorPosition(formatted, oldDigitsBeforeCursor)
            
            onValueChange(
                TextFieldValue(
                    text = formatted,
                    selection = androidx.compose.ui.text.TextRange(newCursorPosition)
                )
            )
        },
        label = { Text("Card Number") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        isError = isError
    )
}