package com.example.todolist.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.todolist.data.Priority
import com.example.todolist.data.Todo
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import java.util.Date

class TodoItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock the callback functions to verify interactions
    private val onToggleComplete: () -> Unit = mockk(relaxed = true)
    private val onEdit: () -> Unit = mockk(relaxed = true)
    private val onDelete: () -> Unit = mockk(relaxed = true)

    @Test
    fun todoItem_displaysAllInformation_forFullTodo() {
        // ARRANGE: Create a Todo object with all fields populated
        val todo = Todo(
            id = 1,
            title = "Buy groceries",
            description = "Milk, bread, and eggs",
            priority = Priority.HIGH,
            dueDate = Date(1672531200000L), // Jan 1, 2023
            isCompleted = false
        )

        // ACT: Set the composable content with the test data
        composeTestRule.setContent {
            TodoItem(
                todo = todo,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }

        // ASSERT: Verify that all the information is displayed correctly
        composeTestRule.onNodeWithText("Buy groceries").assertIsDisplayed()
        composeTestRule.onNodeWithText("Milk, bread, and eggs").assertIsDisplayed()
        composeTestRule.onNodeWithText("High").assertIsDisplayed() // From PriorityChip
        composeTestRule.onNodeWithText("Due: Jan 01, 2023").assertIsDisplayed()

        // Verify the checkbox is not checked
        composeTestRule.onNode(isToggleable()).assertIsOff()
    }

    @Test
    fun todoItem_displaysCorrectly_forMinimalTodo() {
        // ARRANGE: Create a Todo with only the required fields
        val todo = Todo(
            id = 2,
            title = "Walk the dog",
            priority = Priority.LOW,
            isCompleted = true
        )

        // ACT: Set the composable content
        composeTestRule.setContent {
            TodoItem(
                todo = todo,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }

        // ASSERT: Verify the minimal information is displayed
        composeTestRule.onNodeWithText("Walk the dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low").assertIsDisplayed() // From PriorityChip

        // Verify that optional fields are not present
        composeTestRule.onNodeWithText("Due:", substring = true).assertDoesNotExist()

        // Verify the checkbox is checked
        composeTestRule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun whenCheckboxClicked_onToggleCompleteIsCalled() {
        // ARRANGE: Create a simple Todo
        val todo = Todo(id = 3, title = "Test Checkbox", priority = Priority.MEDIUM)

        // ACT: Set the content and perform a click on the Checkbox
        composeTestRule.setContent {
            TodoItem(
                todo = todo,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }

        // Find the checkbox (which is a toggleable component) and click it
        composeTestRule.onNode(isToggleable()).performClick()

        // ASSERT: Verify that the onToggleComplete lambda was called exactly once
        verify(exactly = 1) { onToggleComplete() }
    }

    @Test
    fun whenEditButtonClicked_onEditIsCalled() {
        // ARRANGE: Create a simple Todo
        val todo = Todo(id = 4, title = "Test Edit", priority = Priority.LOW)

        // ACT: Set the content and perform a click on the Edit icon
        composeTestRule.setContent {
            TodoItem(
                todo = todo,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }

        // Find the Edit button by its content description and click it
        composeTestRule.onNodeWithContentDescription("Edit").performClick()

        // ASSERT: Verify that the onEdit lambda was called exactly once
        verify(exactly = 1) { onEdit() }
        verify(exactly = 0) { onDelete() } // Ensure other callbacks were not called
    }

    @Test
    fun whenDeleteButtonClicked_onDeleteIsCalled() {
        // ARRANGE: Create a simple Todo
        val todo = Todo(id = 5, title = "Test Delete", priority = Priority.HIGH)

        // ACT: Set the content and perform a click on the Delete icon
        composeTestRule.setContent {
            TodoItem(
                todo = todo,
                onToggleComplete = onToggleComplete,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }

        // Find the Delete button by its content description and click it
        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // ASSERT: Verify that the onDelete lambda was called exactly once
        verify(exactly = 1) { onDelete() }
        verify(exactly = 0) { onEdit() } // Ensure other callbacks were not called
    }
}
