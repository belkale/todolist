package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.todolist.data.Priority
import com.example.todolist.ui.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    todoId: Long? = null,
    onNavigateBack: () -> Unit,
    todoViewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by todoViewModel.uiState.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val isEditMode = todoId != null
    val currentTodo = if (isEditMode) {
        uiState.todos.find { it.id == todoId }
    } else null

    // Initialize fields when editing
    LaunchedEffect(currentTodo) {
        currentTodo?.let { todo ->
            title = todo.title
            description = todo.description ?: ""
            priority = todo.priority
            dueDate = todo.dueDate
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Todo" else "Add Todo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Priority selection
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        onClick = { priority = p },
                        label = { Text(p.name) },
                        selected = priority == p
                    )
                }
            }

            // Due date section
            Text(
                text = "Due Date (Optional)",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (dueDate != null) {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dueDate)
                    } else {
                        "No due date"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row {
                    if (dueDate != null) {
                        TextButton(onClick = { dueDate = null }) {
                            Text("Clear")
                        }
                    }
                    Button(onClick = { showDatePicker = true }) {
                        Text(if (dueDate == null) "Set Date" else "Change Date")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        if (isEditMode && currentTodo != null) {
                            val updatedTodo = currentTodo.copy(
                                title = title.trim(),
                                description = description.trim().takeIf { it.isNotBlank() },
                                priority = priority,
                                dueDate = dueDate
                            )
                            todoViewModel.updateTodo(updatedTodo)
                        } else {
                            todoViewModel.addTodo(
                                title = title.trim(),
                                description = description.trim().takeIf { it.isNotBlank() },
                                priority = priority,
                                dueDate = dueDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(if (isEditMode) "Update Todo" else "Add Todo")
            }
        }
    }

    // Date picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate?.time ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = Date(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ){
            DatePicker(state = datePickerState)
        }
    }
}
