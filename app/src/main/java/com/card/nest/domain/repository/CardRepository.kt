package com.card.nest.domain.repository

import com.card.nest.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getAllCards(): Flow<List<Card>>
    suspend fun getCardById(id: Long): Card?
    suspend fun insertCard(card: Card): Long
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
}