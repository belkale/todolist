package com.example.todolist.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import com.example.todolist.ui.TodoUiState
import com.example.todolist.ui.TodoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.*

class AddEditTodoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock the ViewModel to control the UI state and verify calls
    private val mockViewModel: TodoViewModel = mockk(relaxed = true)

    // Mock the navigation callback
    private val onNavigateBack: () -> Unit = mockk(relaxed = true)

    @Test
    fun addMode_whenScreenLaunched_displaysEmptyFields() {
        // ARRANGE: Set the UI state to a default empty state
        val emptyUiState = MutableStateFlow(TodoUiState())
        every { mockViewModel.uiState } returns emptyUiState

        // ACT: Launch the composable in "add" mode (todoId is null)
        composeTestRule.setContent {
            AddEditTodoScreen(
                onNavigateBack = onNavigateBack,
                todoViewModel = mockViewModel
            )
        }

        // ASSERT: Verify the UI is in the correct initial state for adding a new todo
        // Check for the "Add Todo" text in the TopAppBar specifically
        composeTestRule.onNode(hasTestTag("TopAppBarTitleText") and hasText("Add Todo")).assertIsDisplayed()
        // Check for the "Add Todo" text on the button
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).assertIsDisplayed()

        // Check that input fields are displayed and empty
        composeTestRule.onNodeWithText("Title").assertTextContains("Title")
        composeTestRule.onNodeWithText("Description (Optional)").assertTextContains("Description (Optional)")

        // Check that MEDIUM priority is selected by default
        composeTestRule.onNode(hasClickAction() and hasText("MEDIUM")).assertIsSelected()
    }

    @Test
    fun editMode_whenScreenLaunched_preFillsFieldsWithTodoData() {
        // ARRANGE: Create a sample todo and set the UI state
        val todoToEdit = Todo(
            id = 123L,
            title = "Existing Task",
            description = "Details of the task",
            priority = Priority.HIGH,
            dueDate = Date()
        )
        // The ViewModel now provides the todo in its state flow
        val uiStateWithTodo = MutableStateFlow(TodoUiState(todos = listOf(todoToEdit)))
        every { mockViewModel.uiState } returns uiStateWithTodo

        // ACT: Launch the composable in "edit" mode by providing a todoId
        composeTestRule.setContent {
            AddEditTodoScreen(
                todoId = 123L,
                onNavigateBack = onNavigateBack,
                todoViewModel = mockViewModel
            )
        }

        // Wait for the LaunchedEffect to run and the UI to recompose.
        composeTestRule.waitForIdle()

        // ASSERT: Verify the fields are pre-filled with the data from the todo
        composeTestRule.onNode(hasTestTag("TopAppBarTitleText") and hasText("Edit Todo")).assertIsDisplayed()

        // The save button text is now "Update Todo"
        composeTestRule.onNode(hasText("Update Todo") and hasClickAction()).assertIsDisplayed()

        // Check that the text fields contain the correct values
        composeTestRule.onNodeWithText("Existing Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("Details of the task").assertIsDisplayed()

        // Verify the HIGH priority chip is selected
        composeTestRule.onNode(hasClickAction() and hasText("HIGH")).assertIsSelected()
        composeTestRule.onNode(hasClickAction() and hasText("MEDIUM")).assertIsNotSelected()
    }

    @Test
    fun saveButton_whenClickedInAddMode_callsViewModelAddTodoAndNavigatesBack() {
        // ARRANGE: Start with an empty state
        val emptyUiState = MutableStateFlow(TodoUiState())
        every { mockViewModel.uiState } returns emptyUiState

        var hasNavigatedBack = false

        // ACT: Launch the composable, simulate user input, and click the save button
        composeTestRule.setContent {
            AddEditTodoScreen(
                onNavigateBack = { hasNavigatedBack = true },
                todoViewModel = mockViewModel
            )
        }

        // Simulate user input
        composeTestRule.onNodeWithText("Title").performTextInput("New Task Title")
        composeTestRule.onNodeWithText("Description (Optional)").performTextInput("Some details")
        composeTestRule.onNodeWithText("LOW").performClick() // Change priority

        // Click the save button (distinguish from title with hasClickAction)
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).performClick()

        // ASSERT: Verify the correct ViewModel function was called with the entered data
        verify {
            mockViewModel.addTodo(
                title = "New Task Title",
                description = "Some details",
                priority = Priority.LOW,
                dueDate = null // We didn't interact with the date picker
            )
        }

        // Also assert that the navigation callback was invoked
        assertTrue(hasNavigatedBack)
    }

    @Test
    fun saveButton_whenTitleIsBlank_isDisabled() {
        // ARRANGE: Start with an empty state
        val emptyUiState = MutableStateFlow(TodoUiState())
        every { mockViewModel.uiState } returns emptyUiState

        // ACT: Launch the composable
        composeTestRule.setContent {
            AddEditTodoScreen(
                onNavigateBack = onNavigateBack,
                todoViewModel = mockViewModel
            )
        }

        // ASSERT: The save button should be disabled because the title is blank
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).assertIsNotEnabled()

        // ACT: Enter some text into the title field
        composeTestRule.onNodeWithText("Title").performTextInput("A valid title")

        // ASSERT: Now the button should be enabled
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).assertIsEnabled()
    }
}
