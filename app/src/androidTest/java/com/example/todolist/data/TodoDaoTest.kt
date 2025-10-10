package com.example.todolist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use an in-memory database for testing to avoid affecting real data
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries() // Allowing main thread queries for simplicity in tests
            .build()
        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTodo_and_getTodoById() = runTest {
        // Given
        val todo = Todo(title = "Test Todo", priority = Priority.LOW)
        val newId = todoDao.insertTodo(todo)

        // When
        val retrievedTodo = todoDao.getTodoById(newId)

        // Then
        assertNotNull(retrievedTodo)
        assertEquals(newId, retrievedTodo?.id)
        assertEquals("Test Todo", retrievedTodo?.title)
    }

    @Test
    fun getAllTodos_returnsAllTodos() = runTest {
        // Given
        val todo1 = Todo(title = "First", priority = Priority.LOW)
        val todo2 = Todo(title = "Second", priority = Priority.HIGH)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        // When
        val allTodos = todoDao.getAllTodos().first() // Use .first() to get the first emitted list from the Flow

        // Then
        assertEquals(2, allTodos.size)
    }

    @Test
    fun updateTodo_reflectsChanges() = runTest {
        // Given
        val todo = Todo(title = "Original Title", priority = Priority.MEDIUM)
        val newId = todoDao.insertTodo(todo)
        val todoToUpdate = todo.copy(id = newId, title = "Updated Title")

        // When
        todoDao.updateTodo(todoToUpdate)
        val retrievedTodo = todoDao.getTodoById(newId)

        // Then
        assertEquals("Updated Title", retrievedTodo?.title)
    }

    @Test
    fun deleteTodo_removesItFromDatabase() = runTest {
        // Given
        val todo = Todo(title = "To be deleted", priority = Priority.LOW)
        val newId = todoDao.insertTodo(todo)
        val todoToDelete = todo.copy(id = newId)

        // When
        todoDao.deleteTodo(todoToDelete)
        val retrievedTodo = todoDao.getTodoById(newId)

        // Then
        assertNull(retrievedTodo)
    }

    @Test
    fun getActiveTodos_returnsOnlyIncomplete() = runTest {
        // Given
        val activeTodo = Todo(title = "Active", priority = Priority.LOW, isCompleted = false)
        val completedTodo = Todo(title = "Done", priority = Priority.LOW, isCompleted = true)
        todoDao.insertTodo(activeTodo)
        todoDao.insertTodo(completedTodo)

        // When
        val activeList = todoDao.getActiveTodos().first()

        // Then
        assertEquals(1, activeList.size)
        assertEquals("Active", activeList[0].title)
    }

    @Test
    fun getCompletedTodos_returnsOnlyCompleted() = runTest {
        // Given
        val activeTodo = Todo(title = "Active", priority = Priority.LOW, isCompleted = false)
        val completedTodo = Todo(title = "Done", priority = Priority.LOW, isCompleted = true)
        todoDao.insertTodo(activeTodo)
        todoDao.insertTodo(completedTodo)

        // When
        val completedList = todoDao.getCompletedTodos().first()

        // Then
        assertEquals(1, completedList.size)
        assertEquals("Done", completedList[0].title)
    }

    @Test
    fun deleteCompletedTodos_removesOnlyCompleted() = runTest {
        // Given
        val activeTodo = Todo(title = "Active", priority = Priority.LOW, isCompleted = false)
        val completedTodo = Todo(title = "Done", priority = Priority.LOW, isCompleted = true)
        todoDao.insertTodo(activeTodo)
        todoDao.insertTodo(completedTodo)

        // When
        todoDao.deleteCompletedTodos()
        val allTodos = todoDao.getAllTodos().first()

        // Then
        assertEquals(1, allTodos.size)
        assertEquals("Active", allTodos[0].title)
    }

    @Test
    fun updateTodoCompletion_updatesStatus() = runTest {
        // Given
        val todo = Todo(title = "To Complete", priority = Priority.HIGH, isCompleted = false)
        val newId = todoDao.insertTodo(todo)

        // When
        todoDao.updateTodoCompletion(id = newId, isCompleted = true)
        val updatedTodo = todoDao.getTodoById(newId)

        // Then
        assertTrue(updatedTodo?.isCompleted ?: false)
    }
}

