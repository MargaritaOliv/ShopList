package com.margaritaolivera.compras.features.lists.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import com.margaritaolivera.compras.features.lists.domain.model.ListItem
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

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

    // Variables para creación/edición
    var selectedItem by remember { mutableStateOf<ListItem?>(null) }
    var itemTitle by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var itemNote by remember { mutableStateOf("") }

    var inviteEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
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
            FloatingActionButton(onClick = {
                itemTitle = ""; itemQuantity = ""; itemNote = ""
                showCreateDialog = true
            }) {
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
                Text("No hay ítems aún.", color = Color.Gray, modifier = Modifier.padding(top = 20.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        Box(modifier = Modifier.clickable {
                            selectedItem = item
                            itemTitle = item.title
                            itemQuantity = item.quantity
                            itemNote = item.note
                            showEditDialog = true
                        }) {
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
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Nuevo ítem") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = itemTitle, onValueChange = { itemTitle = it }, label = { Text("Título") })
                        OutlinedTextField(value = itemQuantity, onValueChange = { itemQuantity = it }, label = { Text("Cantidad") })
                        OutlinedTextField(value = itemNote, onValueChange = { itemNote = it }, label = { Text("Nota") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (itemTitle.isNotBlank()) {
                            viewModel.addItem(itemTitle, itemQuantity, itemNote, listId)
                            showCreateDialog = false
                        }
                    }) { Text("Agregar") }
                },
                dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") } }
            )
        }

        if (showEditDialog && selectedItem != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar ítem") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = itemTitle, onValueChange = { itemTitle = it }, label = { Text("Título") })
                        OutlinedTextField(value = itemQuantity, onValueChange = { itemQuantity = it }, label = { Text("Cantidad") })
                        OutlinedTextField(value = itemNote, onValueChange = { itemNote = it }, label = { Text("Nota") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (itemTitle.isNotBlank()) {
                            viewModel.updateItemContent(selectedItem!!.id, itemTitle, itemQuantity, itemNote)
                            showEditDialog = false
                        }
                    }) { Text("Guardar") }
                },
                dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") } }
            )
        }

        if (showInviteDialog) {
            AlertDialog(
                onDismissRequest = { showInviteDialog = false },
                title = { Text("Invitar Colaborador") },
                text = {
                    Column {
                        Text("Ingresa el correo de la persona.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = inviteEmail, onValueChange = { inviteEmail = it }, label = { Text("Correo") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.inviteUser(inviteEmail, listId)
                        inviteEmail = ""; showInviteDialog = false
                    }) { Text("Invitar") }
                },
                dismissButton = { TextButton(onClick = { showInviteDialog = false }) { Text("Cancelar") } }
            )
        }
    }
}