package com.example.movieapplication.ui.Signin

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.movieapplication.ui.Signin.SigninActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SigninUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SigninActivity>()

    @Test
    fun testAllElementsVisible() {
        composeRule.onNodeWithTag("signup_title").assertIsDisplayed()
        composeRule.onNodeWithTag("username_field").assertIsDisplayed()
        composeRule.onNodeWithTag("email_field").assertIsDisplayed()
        composeRule.onNodeWithTag("password_field").assertIsDisplayed()
        composeRule.onNodeWithTag("confirm_password_field").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_up_button").assertIsDisplayed()
        composeRule.onNodeWithTag("login_text").assertIsDisplayed()
    }

    @Test
    fun testTypingInFields() {
        composeRule.onNodeWithTag("username_field")
            .performTextInput("mahmoud")

        composeRule.onNodeWithTag("email_field")
            .performTextInput("test@example.com")

        composeRule.onNodeWithTag("password_field")
            .performTextInput("Test1234")

        composeRule.onNodeWithTag("confirm_password_field")
            .performTextInput("Test1234")

        // تأكيد إن النص دخل فعلاً
        composeRule.onNodeWithTag("username_field").assertTextContains("mahmoud")
        composeRule.onNodeWithTag("email_field").assertTextContains("test@example.com")
    }

    @Test
    fun testClickSignUp() {
        composeRule.onNodeWithTag("sign_up_button").performClick()
        // هنا مافيش حاجة تحصل UI Navigation لأن Firebase شغال — لكن الاختبار يتأكد أن الضغط حصل
    }

    @Test
    fun testClickLoginText() {
        composeRule.onNodeWithTag("login_text").performClick()
    }
}
