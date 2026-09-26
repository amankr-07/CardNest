package com.card.nest.data.repository

import com.card.nest.data.local.CardDao
import com.card.nest.data.local.CardEntity
import com.card.nest.data.security.EncryptionManager
import com.card.nest.domain.model.Card
import com.card.nest.domain.model.CardNetwork
import com.card.nest.domain.model.CardType
import com.card.nest.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardRepositoryImpl(
    private val cardDao: CardDao,
    private val encryptionManager: EncryptionManager
) : CardRepository {

    override fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards().map { entities ->
            entities.map { it.toDomainModel(encryptionManager) }
        }
    }

    override suspend fun getCardById(id: Long): Card? {
        return cardDao.getCardById(id)?.toDomainModel(encryptionManager)
    }

    override suspend fun insertCard(card: Card): Long {
        val entity = card.toEntity(encryptionManager)
        return cardDao.insertCard(entity)
    }

    override suspend fun updateCard(card: Card) {
        val entity = card.toEntity(encryptionManager).copy(
            updatedAt = System.currentTimeMillis()
        )
        cardDao.updateCard(entity)
    }

    override suspend fun deleteCard(card: Card) {
        val entity = card.toEntity(encryptionManager)
        cardDao.deleteCard(entity)
    }
}

private fun CardEntity.toDomainModel(encryptionManager: EncryptionManager): Card {
    return Card(
        id = id,
        bankName = bankName,
        cardType = CardType.valueOf(cardType),
        cardNetwork = CardNetwork.valueOf(cardNetwork),
        cardHolderName = cardHolderName,
        cardNumber = encryptionManager.decrypt(encryptedCardNumber),
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun Card.toEntity(encryptionManager: EncryptionManager): CardEntity {
    return CardEntity(
        id = id,
        bankName = bankName,
        cardType = cardType.name,
        cardNetwork = cardNetwork.name,
        cardHolderName = cardHolderName,
        encryptedCardNumber = encryptionManager.encrypt(cardNumber),
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}