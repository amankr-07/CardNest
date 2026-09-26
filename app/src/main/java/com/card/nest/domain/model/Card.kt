package com.card.nest.domain.model

data class Card(
    val id: Long = 0,
    val bankName: String,
    val cardType: CardType,
    val cardNetwork: CardNetwork,
    val cardHolderName: String,
    val cardNumber: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)