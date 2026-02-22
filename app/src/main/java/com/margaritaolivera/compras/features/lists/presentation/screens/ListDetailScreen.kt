package com.margaritaolivera.compras.features.lists.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.margaritaolivera.compras.features.lists.presentation.components.ItemRow
import com.margaritaolivera.compras.features.lists.presentation.viewmodels.ListDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    onNavigateBack: () -> Unit,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var showItemDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }

    LaunchedEffect(listId) {
        viewModel.connectToWebSocket(listId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { showInviteDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Invitar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 24.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ID de la lista", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = state.shareCode,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Text(
                text = "Productos / Tareas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.items.isEmpty()) {
                Text(
                    "No hay ítems en este evento aún.",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        ItemRow(
                            item = item,
                            onCheckedChange = { isChecked ->
                                viewModel.toggleItemStatus(item, isChecked)
                            }
                        )
                    }
                }
            }
        }

        if (showItemDialog) {
            AlertDialog(
                onDismissRequest = { showItemDialog = false },
                title = { Text("Nuevo ítem") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad") })
                        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Nota") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addItem(title, quantity, note, listId)
                            title = ""; quantity = ""; note = ""; showItemDialog = false
                        }
                    }) { Text("Agregar") }
                }
            )
        }

        if (showInviteDialog) {
            AlertDialog(
                onDismissRequest = { showInviteDialog = false },
                title = { Text("Invitar Colaborador") },
                text = {
                    Column {
                        Text("Ingresa el correo de la persona que quieres unir a esta lista.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inviteEmail,
                            onValueChange = { inviteEmail = it },
                            label = { Text("Correo electrónico") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (inviteEmail.isNotBlank()) {
                            viewModel.inviteUser(inviteEmail, listId)
                            inviteEmail = ""
                            showInviteDialog = false
                        }
                    }) { Text("Invitar") }
                },
                dismissButton = {
                    TextButton(onClick = { showInviteDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}