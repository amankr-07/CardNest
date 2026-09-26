package com.card.nest

import android.app.Application
import androidx.room.Room
import com.card.nest.data.local.CardDatabase
import com.card.nest.data.repository.CardRepositoryImpl
import com.card.nest.data.security.EncryptionManager
import com.card.nest.domain.repository.CardRepository

class CardNestApplication : Application() {
    
    lateinit var database: CardDatabase
    lateinit var encryptionManager: EncryptionManager
    lateinit var cardRepository: CardRepository

    override fun onCreate() {
        super.onCreate()
        
        database = Room.databaseBuilder(
            applicationContext,
            CardDatabase::class.java,
            "card_database"
        ).build()
        
        encryptionManager = EncryptionManager()
        
        cardRepository = CardRepositoryImpl(
            cardDao = database.cardDao(),
            encryptionManager = encryptionManager
        )
    }
}

val Application.cardRepository: CardRepository
    get() = (this as CardNestApplication).cardRepository