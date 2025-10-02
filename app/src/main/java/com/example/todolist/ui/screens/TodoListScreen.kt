package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import com.example.todolist.ui.TodoFilter
import com.example.todolist.ui.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToAddTodo: () -> Unit,
    onNavigateToEditTodo: (Long) -> Unit,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredTodos = remember(uiState.todos, uiState.filter) {
        viewModel.getFilteredTodos()
    }

    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Todos") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        TodoFilter.entries.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter.name) },
                                onClick = {
                                    viewModel.setFilter(filter)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTodo
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TodoFilter.entries.forEach { filter ->
                    FilterChip(
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.name) },
                        selected = uiState.filter == filter
                    )
                }
            }

            // Todo list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTodos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (uiState.filter) {
                            TodoFilter.ALL -> "No todos yet. Add one!"
                            TodoFilter.ACTIVE -> "No active todos"
                            TodoFilter.COMPLETED -> "No completed todos"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTodos) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggleComplete = { viewModel.toggleTodoCompletion(todo) },
                            onEdit = { onNavigateToEditTodo(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }
                // Clear completed button
                if (uiState.todos.any { it.isCompleted }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { viewModel.deleteCompletedTodos() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Completed")
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        leadingContent = { Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { onToggleComplete() }
        ) },
        headlineContent = {
            Text(
                text = todo.title,
            )
        },
        overlineContent = {PriorityChip(priority = todo.priority)},
        supportingContent =  {
            Column {
                todo.description?.let { description ->
                    Text(
                        text = description,
                    )
                }
                todo.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dueDate)}",
                    )
                }
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton (
                    onClick = onDelete,
                ) {
                    Icon(imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
fun PriorityChip(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.LOW -> MaterialTheme.colorScheme.primary to "Low"
        Priority.MEDIUM -> MaterialTheme.colorScheme.secondary to "Medium"
        Priority.HIGH -> MaterialTheme.colorScheme.error to "High"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
