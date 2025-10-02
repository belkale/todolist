package com.example.todolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            repository.getAllTodos().collect { todos ->
                _uiState.value = _uiState.value.copy(
                    todos = todos,
                    isLoading = false
                )
            }
        }
    }

    fun addTodo(title: String, description: String?, priority: Priority, dueDate: Date?) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val todo = Todo(
                title = title.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                priority = priority,
                dueDate = dueDate
            )
            repository.insertTodo(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun toggleTodoCompletion(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodoCompletion(todo.id, !todo.isCompleted)
        }
    }

    fun deleteCompletedTodos() {
        viewModelScope.launch {
            repository.deleteCompletedTodos()
        }
    }

    fun setFilter(filter: TodoFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun getFilteredTodos(): List<Todo> {
        val todos = _uiState.value.todos
        return when (_uiState.value.filter) {
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.isCompleted }
            TodoFilter.COMPLETED -> todos.filter { it.isCompleted }
        }
    }
}

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = true,
    val filter: TodoFilter = TodoFilter.ALL
)

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}
