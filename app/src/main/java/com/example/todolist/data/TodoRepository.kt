package com.example.todolist.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()

    fun getActiveTodos(): Flow<List<Todo>> = todoDao.getActiveTodos()

    fun getCompletedTodos(): Flow<List<Todo>> = todoDao.getCompletedTodos()

    suspend fun getTodoById(id: Long): Todo? = todoDao.getTodoById(id)

    suspend fun insertTodo(todo: Todo): Long = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun deleteCompletedTodos() = todoDao.deleteCompletedTodos()

    suspend fun updateTodoCompletion(id: Long, isCompleted: Boolean) = 
        todoDao.updateTodoCompletion(id, isCompleted)

    suspend fun clearAllTodos() {
        todoDao.clearAllTodos()
    }
}
