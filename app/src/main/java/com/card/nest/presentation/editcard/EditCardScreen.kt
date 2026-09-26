package com.card.nest.presentation.editcard

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.card.nest.domain.model.CardNetwork
import com.card.nest.domain.model.CardType
import com.card.nest.ui.components.CardNumberTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScreen(
    cardId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditCardViewModel = viewModel(
        factory = EditCardViewModelFactory(cardId, LocalContext.current.applicationContext as Application)
    )
) {
    val bankName by viewModel.bankName.collectAsState()
    val cardType by viewModel.cardType.collectAsState()
    val cardNetwork by viewModel.cardNetwork.collectAsState()
    val cardHolderName by viewModel.cardHolderName.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()
    val expiryMonth by viewModel.expiryMonth.collectAsState()
    val expiryYear by viewModel.expiryYear.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bank Name
                OutlinedTextField(
                    value = bankName,
                    onValueChange = viewModel::onBankNameChange,
                    label = { Text("Bank / Issuer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && bankName.isEmpty()
                )
                
                // Card Type
                Text(
                    text = "Card Type",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CardType.values().forEach { type ->
                        FilterChip(
                            selected = cardType == type,
                            onClick = { viewModel.onCardTypeChange(type) },
                            label = { Text(type.name.replace("_", " ")) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Card Network
                Text(
                    text = "Card Network",
                    style = MaterialTheme.typography.labelLarge
                )
                var networkExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = networkExpanded,
                    onExpandedChange = { networkExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = cardNetwork?.name?.replace("_", " ") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Card Network") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = networkExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = networkExpanded,
                        onDismissRequest = { networkExpanded = false }
                    ) {
                        CardNetwork.values().forEach { network ->
                            DropdownMenuItem(
                                text = { Text(network.name.replace("_", " ")) },
                                onClick = {
                                    viewModel.onCardNetworkChange(network)
                                    networkExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Cardholder Name
                OutlinedTextField(
                    value = cardHolderName,
                    onValueChange = viewModel::onCardHolderNameChange,
                    label = { Text("Cardholder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && cardHolderName.isEmpty()
                )
                
                // Card Number
                CardNumberTextField(
                    value = cardNumber,
                    onValueChange = viewModel::onCardNumberChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && cardNumber.text.filter { it.isDigit() }.length < 13
                )
                
                // Expiry
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = expiryMonth,
                        onValueChange = viewModel::onExpiryMonthChange,
                        label = { Text("MM") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("MM") }
                    )
                    OutlinedTextField(
                        value = expiryYear,
                        onValueChange = viewModel::onExpiryYearChange,
                        label = { Text("YY") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("YY") }
                    )
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Notes (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.saveCard(onSuccess) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Update Card")
                    }
                }
            }
        }
    }
}