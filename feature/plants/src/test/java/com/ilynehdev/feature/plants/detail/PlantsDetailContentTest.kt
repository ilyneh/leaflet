package com.ilynehdev.feature.plants.detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantsDetailContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(resId: Int) = composeRule.activity.getString(resId)

    private fun setContent(
        uiState: PlantsDetailUiState,
        onSaveClicked: () -> Unit = {},
        onRetryClicked: () -> Unit = {},
        onPullToRefresh: () -> Unit = {},
    ) {
        composeRule.setContent {
            LeafletTheme {
                PlantsDetailContent(
                    uiState = uiState,
                    onBackClicked = {},
                    onSaveClicked = onSaveClicked,
                    onRetryClicked = onRetryClicked,
                    onPullToRefresh = onPullToRefresh,
                )
            }
        }
    }

    private fun plant(commonName: String = "Monstera") = PlantsDetailUiData(
        commonName = commonName,
        latinName = "Monstera deliciosa",
        imageUrl = null,
        description = "A climbing evergreen.",
        lightLabel = "Bright indirect",
        waterLabel = "Every 7 days",
        matureSize = "2–3 m",
        careLevel = "Easy",
        toxicToPets = true,
        toxicityNotice = PlantsDetailUiData.ToxicityNotice.Pets,
    )

    @Test
    fun `plant content is displayed`() {
        setContent(
            PlantsDetailUiState(
                plant = plant(),
                saveButtonVisible = true,
                loadingStatus = LoadingStatus.Done,
            )
        )

        composeRule.onNodeWithText("Monstera").assertIsDisplayed()
        composeRule.onNodeWithText("Monstera deliciosa").assertIsDisplayed()
        composeRule.onNodeWithText("A climbing evergreen.").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.save)).assertIsDisplayed()
    }

    @Test
    fun `failed load without plant shows error and retry fires callback`() {
        var retried = false
        setContent(
            PlantsDetailUiState(loadingStatus = LoadingStatus.Failed),
            onRetryClicked = { retried = true },
        )

        composeRule.onNodeWithText(string(R.string.plant_detail_load_failed)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.retry)).performClick()

        assertTrue(retried)
    }

    @Test
    fun `save button is not selected when unsaved`() {
        setContent(
            PlantsDetailUiState(
                plant = plant(),
                saveButtonVisible = true,
                isSaved = false,
                loadingStatus = LoadingStatus.Done,
            )
        )

        composeRule.onNodeWithContentDescription(string(R.string.save)).assertIsNotSelected()
    }

    @Test
    fun `save button is selected when saved`() {
        setContent(
            PlantsDetailUiState(
                plant = plant(),
                saveButtonVisible = true,
                isSaved = true,
                loadingStatus = LoadingStatus.Done,
            )
        )

        composeRule.onNodeWithContentDescription(string(R.string.save)).assertIsSelected()
    }

    @Test
    fun `swipe down triggers pull to refresh`() {
        var refreshed = false
        setContent(
            PlantsDetailUiState(
                plant = plant(),
                saveButtonVisible = true,
                loadingStatus = LoadingStatus.Done,
            ),
            onPullToRefresh = { refreshed = true },
        )

        composeRule.onRoot().performTouchInput { swipeDown() }
        composeRule.waitForIdle()

        assertTrue(refreshed)
    }
}
