package com.example.todolist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.data.TodoRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TodoAppIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: TodoRepository

    @Before 
    fun setUp() {
        hiltRule.inject() // Inject dependencies
        runBlocking {
            repository.clearAllTodos() // Clear the database
        }
    }
    @Test
    fun app_launches_and_displaysTodoListScreen() {
        // ASSERT: Check that the main screen is displayed by verifying its title
        // and the Floating Action Button for adding a new todo.
        composeTestRule.onNodeWithText("My Todos").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Todo").assertIsDisplayed()
    }

    @Test
    fun full_addTodo_flow() {
        val newTodoTitle = "My New Integration Test Todo"
        val newTodoDescription = "This is a test description."

        // 1. Start on the list screen and click the FAB to navigate.
        composeTestRule.onNodeWithContentDescription("Add Todo").performClick()
        composeTestRule.waitForIdle() // Wait for navigation to complete

        // 2. We are now on the AddEditTodoScreen. Check for its title.
        composeTestRule.onNode(hasTestTag("TopAppBarTitleText") and hasText("Add Todo")).assertIsDisplayed()

        // 3. Fill in the form fields.
        composeTestRule.onNodeWithText("Title").performTextInput(newTodoTitle)
        composeTestRule.onNodeWithText("Description (Optional)").performTextInput(newTodoDescription)
        composeTestRule.onNodeWithText("HIGH").performClick() // Change priority

        // 4. Click the save button.
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).performClick()
        composeTestRule.waitForIdle() // Wait for navigation back to the list

        // 5. Back on the list screen, verify the new todo item is displayed.
        composeTestRule.onNodeWithText("My Todos").assertIsDisplayed()
        composeTestRule.onNodeWithText(newTodoTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(newTodoDescription).assertIsDisplayed()
    }

    @Test
    fun addTodo_then_editIt() {
        val originalTitle = "Todo to be edited"
        val updatedTitle = "This title has been updated"

        // ARRANGE: Add a new todo to work with
        composeTestRule.onNodeWithContentDescription("Add Todo").performClick()
        composeTestRule.onNodeWithText("Title").performTextInput(originalTitle)
        composeTestRule.onNode(hasText("Add Todo") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // ACT:
        // 1. Find the parent ListItem that contains our title, and then find the Edit button within it.
        composeTestRule.onNode(hasAnyAncestor(hasAnyDescendant(hasText(originalTitle))) and hasContentDescription("Edit")).performClick()

        composeTestRule.waitForIdle() // Wait for navigation to the edit screen

        // 2. Verify we are on the "Edit Todo" screen.
        composeTestRule.onNode(hasTestTag("TopAppBarTitleText") and hasText("Edit Todo")).assertIsDisplayed()

        // 3. The original title should be pre-filled. Clear it and type the new title.
        composeTestRule.onNodeWithText("Title").performTextClearance()
        composeTestRule.onNodeWithText("Title").performTextInput(updatedTitle)

        // 4. Click the "Update Todo" button.
        composeTestRule.onNode(hasText("Update Todo") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // ASSERT:
        // 1. We are back on the list screen.
        composeTestRule.onNodeWithText("My Todos").assertIsDisplayed()

        // 2. The original title should no longer exist.
        composeTestRule.onNodeWithText(originalTitle).assertDoesNotExist()

        // 3. The new, updated title should be visible.
        composeTestRule.onNodeWithText(updatedTitle).assertIsDisplayed()
    }
}

