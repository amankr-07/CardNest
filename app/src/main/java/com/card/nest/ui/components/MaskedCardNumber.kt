package com.card.nest.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MaskedCardNumber(
    cardNumber: String,
    modifier: Modifier = Modifier
) {
    val normalizedNumber = cardNumber.replace(" ", "")
    val maskedNumber = if (normalizedNumber.length >= 4) {
        "•••• •••• •••• ${normalizedNumber.takeLast(4)}"
    } else {
        "•••• •••• •••• ••••"
    }
    
    androidx.compose.material3.Text(
        text = maskedNumber,
        modifier = modifier,
        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
    )
}