package com.example.todolist.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import com.example.todolist.ui.TodoFilter
import com.example.todolist.ui.TodoUiState
import com.example.todolist.ui.TodoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class TodoListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock the ViewModel to control the UI state
    private val mockViewModel: TodoViewModel = mockk(relaxed = true)

    // Mock the navigation callbacks
    private val onNavigateToAddTodo: () -> Unit = mockk(relaxed = true)
    private val onNavigateToEditTodo: (Long) -> Unit = mockk(relaxed = true)

    @Test
    fun whenStateIsLoading_circularProgressIndicatorIsDisplayed() {
        // ARRANGE: Set the UI state to loading
        val loadingState = MutableStateFlow(TodoUiState(isLoading = true))
        every { mockViewModel.uiState } returns loadingState

        // ACT: Set the content with the mocked ViewModel
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }

        // ASSERT: Verify the loading indicator is shown and the list is not
        composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertIsDisplayed()
        composeTestRule.onNodeWithText("No todos yet. Add one!").assertDoesNotExist()
    }

    @Test
    fun whenListIsEmpty_showsEmptyMessageForActiveFilter() {
        // ARRANGE: Set the state to not loading, empty list, with ACTIVE filter
        val emptyState = MutableStateFlow(
            TodoUiState(
                isLoading = false,
                todos = emptyList(),
                filter = TodoFilter.ACTIVE
            )
        )
        every { mockViewModel.uiState } returns emptyState
        every { mockViewModel.getFilteredTodos() } returns emptyList()

        // ACT: Set the content
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }

        // ASSERT: Verify the correct empty message for the "ACTIVE" filter is shown
        composeTestRule.onNodeWithText("No active todos").assertIsDisplayed()
    }

    @Test
    fun whenTodosExist_lazyColumnWithItemsIsDisplayed() {
        // ARRANGE: Set a state with a list of todos
        val todos = listOf(
            Todo(id = 1, title = "First Todo", priority = Priority.LOW),
            Todo(id = 2, title = "Second Todo", priority = Priority.HIGH, isCompleted = true)
        )
        val dataState = MutableStateFlow(TodoUiState(isLoading = false, todos = todos))
        every { mockViewModel.uiState } returns dataState
        every { mockViewModel.getFilteredTodos() } returns todos

        // ACT: Set the content
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }

        // ASSERT: Verify that the todo items are displayed in the list
        composeTestRule.onNodeWithText("First Todo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Todo").assertIsDisplayed()
        // And the empty message is not shown
        composeTestRule.onNodeWithText("No todos yet. Add one!").assertDoesNotExist()
    }

    @Test
    fun clickingFilterChip_callsViewModelSetFilter() {
        // ARRANGE: Set a default state
        val initialState = MutableStateFlow(TodoUiState(isLoading = false))
        every { mockViewModel.uiState } returns initialState

        // ACT: Set the content and click the "COMPLETED" filter chip
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }
        composeTestRule.onNodeWithText("COMPLETED").performClick()

        // ASSERT: Verify the ViewModel function was called with the correct filter
        verify(exactly = 1) { mockViewModel.setFilter(TodoFilter.COMPLETED) }
    }

    @Test
    fun clickingFab_triggersOnNavigateToAddTodoCallback() {
        // ARRANGE: Set a default state
        val initialState = MutableStateFlow(TodoUiState(isLoading = false))
        every { mockViewModel.uiState } returns initialState

        // ACT: Set the content and click the Floating Action Button
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }
        composeTestRule.onNodeWithContentDescription("Add Todo").performClick()

        // ASSERT: Verify that the navigation callback was triggered
        verify(exactly = 1) { onNavigateToAddTodo() }
    }

    @Test
    fun whenCompletedTodosExist_clearCompletedButtonIsDisplayed() {
        // ARRANGE: Set a state where at least one todo is completed
        val todos = listOf(Todo(id = 1, title = "Done", priority = Priority.LOW, isCompleted = true))
        val dataState = MutableStateFlow(TodoUiState(isLoading = false, todos = todos))
        every { mockViewModel.uiState } returns dataState
        every { mockViewModel.getFilteredTodos() } returns todos


        // ACT: Set the content
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }

        // ASSERT: Verify the "Clear Completed" button is displayed
        composeTestRule.onNodeWithText("Clear Completed").assertIsDisplayed()
    }

    @Test
    fun clickingClearCompleted_callsViewModelFunction() {
        // ARRANGE: Set a state with a completed todo so the button is visible
        val todos = listOf(Todo(id = 1, title = "Done", priority = Priority.LOW, isCompleted = true))
        val dataState = MutableStateFlow(TodoUiState(isLoading = false, todos = todos))
        every { mockViewModel.uiState } returns dataState
        every { mockViewModel.getFilteredTodos() } returns todos

        // ACT: Set the content and click the button
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }
        composeTestRule.onNodeWithText("Clear Completed").performClick()

        // ASSERT: Verify the correct ViewModel function was called
        verify(exactly = 1) { mockViewModel.deleteCompletedTodos() }
    }

    @Test
    fun clickingEditOnTodoItem_triggersOnNavigateToEditTodoCallback() {
        // ARRANGE: Set state with one item
        val todo = Todo(id = 123L, title = "Todo to Edit", priority = Priority.MEDIUM)
        val dataState = MutableStateFlow(TodoUiState(isLoading = false, todos = listOf(todo)))
        every { mockViewModel.uiState } returns dataState
        every { mockViewModel.getFilteredTodos() } returns listOf(todo)

        // ACT: Set content and click the edit icon on the item
        composeTestRule.setContent {
            TodoListScreen(
                onNavigateToAddTodo = onNavigateToAddTodo,
                onNavigateToEditTodo = onNavigateToEditTodo,
                viewModel = mockViewModel
            )
        }
        composeTestRule.onNodeWithContentDescription("Edit").performClick()

        // ASSERT: Verify the navigation callback was called with the correct ID
        verify(exactly = 1) { onNavigateToEditTodo(123L) }
    }
}
