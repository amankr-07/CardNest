package com.card.nest.presentation.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.card.nest.CardNestApplication
import com.card.nest.domain.repository.CardRepository

class CardDetailsViewModelFactory(
    private val cardId: Long,
    private val application: Application
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardDetailsViewModel::class.java)) {
            val cardRepository = (application as CardNestApplication).cardRepository
            return CardDetailsViewModel(cardRepository, cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}