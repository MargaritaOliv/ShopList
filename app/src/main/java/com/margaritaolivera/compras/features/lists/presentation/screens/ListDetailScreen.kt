package com.margaritaolivera.compras.features.lists.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    var showItemDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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
                        Icon(Icons.Default.Person, contentDescription = "Invitar")
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

            Text(
                text = "Productos / Tareas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
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
                                viewModel.toggleItemStatus(item, isCompleted = isChecked)
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
                },
                dismissButton = {
                    TextButton(onClick = { showItemDialog = false }) { Text("Cancelar") }
                }
            )
        }

        if (showInviteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showInviteDialog = false
                    emailError = null
                },
                title = { Text("Invitar Colaborador") },
                text = {
                    Column {
                        Text("Ingresa el correo de la persona que quieres unir a esta lista.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inviteEmail,
                            onValueChange = {
                                inviteEmail = it
                                emailError = null
                            },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            isError = emailError != null,
                            supportingText = {
                                if (emailError != null) {
                                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (inviteEmail.isNotBlank()) {
                            if (!inviteEmail.contains("@")) {
                                emailError = "Ingresa un correo válido"
                            } else {
                                viewModel.inviteUser(inviteEmail, listId)
                                inviteEmail = ""
                                showInviteDialog = false
                            }
                        }
                    }) { Text("Invitar") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showInviteDialog = false
                        emailError = null
                    }) { Text("Cancelar") }
                }
            )
        }
    }
}