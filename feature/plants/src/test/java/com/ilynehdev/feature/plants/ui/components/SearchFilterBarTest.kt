package com.ilynehdev.feature.plants.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFilterBarTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(resId: Int) = composeRule.activity.getString(resId)

    private fun setContent(
        activeFilterCount: Int = 0,
        onFilterClicked: () -> Unit = {},
    ) {
        composeRule.setContent {
            LeafletTheme {
                SearchFilterBar(
                    searchQuery = "",
                    onSearchQueryChanged = {},
                    onFilterClicked = onFilterClicked,
                    activeFilterCount = activeFilterCount,
                )
            }
        }
    }

    @Test
    fun `badge is hidden when no filters are active`() {
        setContent(activeFilterCount = 0)

        composeRule.onNodeWithText("0").assertDoesNotExist()
    }

    @Test
    fun `badge shows the active filter count`() {
        setContent(activeFilterCount = 3)

        composeRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun `filter button fires callback`() {
        var clicked = false
        setContent(onFilterClicked = { clicked = true })

        composeRule.onNodeWithContentDescription(string(R.string.filter)).performClick()

        assertTrue(clicked)
    }
}
