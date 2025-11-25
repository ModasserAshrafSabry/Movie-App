package com.example.movieapplication.ui.Login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.movieapplication.ui.Login.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun loginScreen_elementsAreDisplayed() {

        composeRule.waitForIdle()


        composeRule.onNodeWithTag("login_title").assertIsDisplayed()
        composeRule.onNodeWithTag("email_field").assertIsDisplayed()
        composeRule.onNodeWithTag("password_field").assertIsDisplayed()
        composeRule.onNodeWithTag("forgot_password_text").assertIsDisplayed()
        composeRule.onNodeWithTag("login_button").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_up_text").assertIsDisplayed()
    }

    @Test
    fun loginScreen_interactionTest() {
        composeRule.waitForIdle()


        composeRule.onNodeWithTag("email_field").performTextInput("test@example.com")
        composeRule.onNodeWithTag("password_field").performTextInput("Password123")


        composeRule.onNodeWithTag("login_button")
            .assertIsDisplayed()
            .performClick()


        composeRule.onNodeWithTag("forgot_password_text")
            .assertIsDisplayed()
            .performClick()


        composeRule.onNodeWithTag("sign_up_text")
            .assertIsDisplayed()
            .performClick()
    }
}
