package com.card.nest.presentation.addcard

import android.app.Application
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.card.nest.CardNestApplication
import com.card.nest.domain.model.Card
import com.card.nest.domain.model.CardNetwork
import com.card.nest.domain.model.CardType
import com.card.nest.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AddCardViewModel(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _bankName = MutableStateFlow("")
    val bankName: StateFlow<String> = _bankName.asStateFlow()

    private val _cardType = MutableStateFlow<CardType?>(null)
    val cardType: StateFlow<CardType?> = _cardType.asStateFlow()

    private val _cardNetwork = MutableStateFlow<CardNetwork?>(null)
    val cardNetwork: StateFlow<CardNetwork?> = _cardNetwork.asStateFlow()

    private val _cardHolderName = MutableStateFlow("")
    val cardHolderName: StateFlow<String> = _cardHolderName.asStateFlow()

    private val _cardNumber = MutableStateFlow(TextFieldValue(""))
    val cardNumber: StateFlow<TextFieldValue> = _cardNumber.asStateFlow()

    private val _expiryMonth = MutableStateFlow("")
    val expiryMonth: StateFlow<String> = _expiryMonth.asStateFlow()

    private val _expiryYear = MutableStateFlow("")
    val expiryYear: StateFlow<String> = _expiryYear.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onBankNameChange(value: String) {
        _bankName.value = value
        _errorMessage.value = null
    }

    fun onCardTypeChange(type: CardType) {
        _cardType.value = type
        _errorMessage.value = null
    }

    fun onCardNetworkChange(network: CardNetwork) {
        _cardNetwork.value = network
        _errorMessage.value = null
    }

    fun onCardHolderNameChange(value: String) {
        _cardHolderName.value = value
        _errorMessage.value = null
    }

    fun onCardNumberChange(value: TextFieldValue) {
        _cardNumber.value = value
        _errorMessage.value = null
    }

    fun onExpiryMonthChange(value: String) {
        _expiryMonth.value = value.take(2)
        _errorMessage.value = null
    }

    fun onExpiryYearChange(value: String) {
        _expiryYear.value = value.take(2)
        _errorMessage.value = null
    }

    fun onNotesChange(value: String) {
        _notes.value = value
    }

    fun saveCard(onSuccess: () -> Unit) {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val card = Card(
                    bankName = _bankName.value.trim(),
                    cardType = _cardType.value!!,
                    cardNetwork = _cardNetwork.value!!,
                    cardHolderName = _cardHolderName.value.trim(),
                    cardNumber = _cardNumber.value.text.filter { it.isDigit() },
                    expiryMonth = _expiryMonth.value.toInt(),
                    expiryYear = 2000 + _expiryYear.value.toInt(),
                    notes = _notes.value.trim()
                )

                cardRepository.insertCard(card)
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save card: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInput(): Boolean {
        if (_bankName.value.trim().isEmpty()) {
            _errorMessage.value = "Please enter a bank name"
            return false
        }

        if (_cardType.value == null) {
            _errorMessage.value = "Please select a card type"
            return false
        }

        if (_cardNetwork.value == null) {
            _errorMessage.value = "Please select a card network"
            return false
        }

        if (_cardHolderName.value.trim().isEmpty()) {
            _errorMessage.value = "Please enter a cardholder name"
            return false
        }

        val normalizedCardNumber = _cardNumber.value.text.filter { it.isDigit() }
        if (normalizedCardNumber.length < 13 || normalizedCardNumber.length > 19) {
            _errorMessage.value = "Please enter a valid card number"
            return false
        }

        if (!isValidLuhn(normalizedCardNumber)) {
            _errorMessage.value = "Please enter a valid card number"
            return false
        }

        if (_expiryMonth.value.isEmpty() || _expiryYear.value.isEmpty()) {
            _errorMessage.value = "Please enter a valid expiry date"
            return false
        }

        val month = _expiryMonth.value.toIntOrNull()
        val year = 2000 + (_expiryYear.value.toIntOrNull() ?: 0)

        if (month == null || month < 1 || month > 12) {
            _errorMessage.value = "Please enter a valid month (01-12)"
            return false
        }

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            _errorMessage.value = "Card has expired"
            return false
        }

        return true
    }

    private fun isValidLuhn(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var digit = cardNumber[i].digitToInt()
            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10)
                }
            }
            sum += digit
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    private fun clearForm() {
        _bankName.value = ""
        _cardType.value = null
        _cardNetwork.value = null
        _cardHolderName.value = ""
        _cardNumber.value = ""
        _expiryMonth.value = ""
        _expiryYear.value = ""
        _notes.value = ""
        _errorMessage.value = null
    }
}

class AddCardViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCardViewModel::class.java)) {
            val cardRepository = (application as CardNestApplication).cardRepository
            return AddCardViewModel(cardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}