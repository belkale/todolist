package com.example.todolist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoRepository
import com.example.todolist.util.MainCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@ExperimentalCoroutinesApi
class TodoViewModelTest {

    // Rule to swap the main dispatcher for a test dispatcher
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Rule to execute LiveData/Flow emissions synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Mock the repository dependency
    private val repository: TodoRepository = mockk(relaxed = true)

    // The ViewModel instance we will be testing
    private lateinit var viewModel: TodoViewModel

    // A sample list of todos for our tests
    private val sampleTodos = listOf(
        Todo(1, "Active Todo", isCompleted = false, priority = Priority.LOW, dueDate = Date()),
        Todo(2, "Completed Todo", isCompleted = true, priority = Priority.HIGH, dueDate = Date())
    )

    @Before
    fun setup() {
        // Before each test, set up the mock repository to return our sample data
        every { repository.getAllTodos() } returns flowOf(sampleTodos)

        // Create a new ViewModel instance
        viewModel = TodoViewModel(repository)
    }

    @Test
    fun `loadTodos updates uiState with todos and sets loading to false`() {
        // The `init` block already calls `loadTodos`, so we just need to verify the state.
        val uiState = viewModel.uiState.value

        assertEquals(false, uiState.isLoading)
        assertEquals(2, uiState.todos.size)
        assertEquals("Active Todo", uiState.todos[0].title)
    }

    @Test
    fun `addTodo calls repository insertTodo`() = runTest {
        // Given a new todo's details
        val title = "New Task"
        val description = "Task Description"
        val priority = Priority.MEDIUM
        val dueDate = Date()

        // When addTodo is called
        viewModel.addTodo(title, description, priority, dueDate)

        // Then verify that the repository's insertTodo function was called with the correct data
        val todoSlot = slot<Todo>()
        coVerify { repository.insertTodo(capture(todoSlot)) }

        assertEquals(title, todoSlot.captured.title)
        assertEquals(description, todoSlot.captured.description)
        assertEquals(priority, todoSlot.captured.priority)
    }

    @Test
    fun `addTodo does not call repository if title is blank`() = runTest {
        // When addTodo is called with a blank title
        viewModel.addTodo("   ", "description", Priority.LOW, null)

        // Then verify that insertTodo was never called
        coVerify(exactly = 0) { repository.insertTodo(any()) }
    }

    @Test
    fun `toggleTodoCompletion calls repository with correct new state`() = runTest {
        // Given the first todo from our sample list (which is not completed)
        val todoToToggle = sampleTodos[0]

        // When completion is toggled
        viewModel.toggleTodoCompletion(todoToToggle)

        // Then verify repository's update function is called with the correct ID and the new completion status (true)
        coVerify { repository.updateTodoCompletion(todoToToggle.id, true) }
    }

    @Test
    fun `deleteCompletedTodos calls repository deleteCompletedTodos`() = runTest {
        // When deleteCompletedTodos is called
        viewModel.deleteCompletedTodos()

        // Then verify the corresponding repository function is called
        coVerify { repository.deleteCompletedTodos() }
    }

    @Test
    fun `getFilteredTodos returns all todos when filter is ALL`() {
        // Given the filter is set to ALL (default)
        viewModel.setFilter(TodoFilter.ALL)

        // When getFilteredTodos is called
        val filtered = viewModel.getFilteredTodos()

        // Then the list contains all todos
        assertEquals(2, filtered.size)
    }

    @Test
    fun `getFilteredTodos returns only active todos when filter is ACTIVE`() {
        // Given the filter is set to ACTIVE
        viewModel.setFilter(TodoFilter.ACTIVE)

        // When getFilteredTodos is called
        val filtered = viewModel.getFilteredTodos()

        // Then the list contains only the active todo
        assertEquals(1, filtered.size)
        assertEquals("Active Todo", filtered[0].title)
    }

    @Test
    fun `getFilteredTodos returns only completed todos when filter is COMPLETED`() {
        // Given the filter is set to COMPLETED
        viewModel.setFilter(TodoFilter.COMPLETED)

        // When getFilteredTodos is called
        val filtered = viewModel.getFilteredTodos()

        // Then the list contains only the completed todo
        assertEquals(1, filtered.size)
        assertEquals("Completed Todo", filtered[0].title)
    }
}
