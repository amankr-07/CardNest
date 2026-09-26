package com.card.nest.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.card.nest.domain.model.Card
import com.card.nest.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardDetailsViewModel(
    private val cardRepository: CardRepository,
    private val cardId: Long
) : ViewModel() {

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isNumberVisible = MutableStateFlow(false)
    val isNumberVisible: StateFlow<Boolean> = _isNumberVisible.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCard()
    }

    private fun loadCard() {
        viewModelScope.launch {
            _isLoading.value = true
            _card.value = cardRepository.getCardById(cardId)
            _isLoading.value = false
        }
    }

    fun revealCardNumber() {
        _isNumberVisible.value = true
    }

    fun hideCardNumber() {
        _isNumberVisible.value = false
    }

    fun deleteCard(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _card.value?.let { card ->
                cardRepository.deleteCard(card)
                onSuccess()
            }
        }
    }
}