package com.br444n.constructionmaterialtrack.presentation.components.progress

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class GlassLinearProgressBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun glassProgressBar_displaysCorrectProgressText() {
        composeTestRule.setContent {
            GlassLinearProgressBar(
                progress = 0.5f,
                totalMaterials = 10,
                completedMaterials = 5
            )
        }

        // Verify that the title text is displayed
        composeTestRule.onNodeWithText("Project Progress").assertIsDisplayed()

        // Verify that the materials count is displayed correctly
        composeTestRule.onNodeWithText("5/10 materials").assertIsDisplayed()

        // Verify that the percentage is displayed correctly (0.5 progress = 50%)
        composeTestRule.onNodeWithText("50%").assertIsDisplayed()
    }

    @Test
    fun glassProgressBar_displaysHundredPercentCorrectly() {
        composeTestRule.setContent {
            GlassLinearProgressBar(
                progress = 1.0f,
                totalMaterials = 20,
                completedMaterials = 20
            )
        }

        composeTestRule.onNodeWithText("20/20 materials").assertIsDisplayed()
        composeTestRule.onNodeWithText("100%").assertIsDisplayed()
    }
}
