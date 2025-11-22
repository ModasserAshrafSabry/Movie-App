package com.example.movieapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.movieapplication.ui.Splash.SplashActivity
import org.junit.Rule
import org.junit.Test

class SplashUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SplashActivity>()

    @Test
    fun splashScreen_elementsAreDisplayed() {

        composeRule.waitForIdle()


        composeRule.onNodeWithTag("welcome_text")
            .assertIsDisplayed()


        composeRule.onNodeWithTag("app_name_text")
            .assertIsDisplayed()


        composeRule.onNodeWithTag("splash_description")
            .assertIsDisplayed()


        composeRule.onNodeWithTag("next_button")
            .assertIsDisplayed()
    }

    @Test
    fun splashScreen_nextButtonWorks() {

        composeRule.waitForIdle()


        composeRule.onNodeWithTag("next_button")
            .assertIsDisplayed()
            .performClick()
    }
}
