package com.card.nest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankName: String,
    val cardType: String,
    val cardNetwork: String,
    val cardHolderName: String,
    val encryptedCardNumber: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)