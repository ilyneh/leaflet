package com.ilynehdev.feature.plants.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.feature.plants.R
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h1200dp")
class PlantsListContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(resId: Int) = composeRule.activity.getString(resId)

    private fun setContent(
        items: List<PlantsListItemUiData> = plants(),
        searchQuery: String = "",
        showSavedOnly: Boolean = false,
        filters: PlantsListFiltersUiData = PlantsListFiltersUiData(),
        onSearchQueryChanged: (String) -> Unit = {},
        onFiltersApplied: (PlantFilters) -> Unit = {},
        toggleShowSaved: () -> Unit = {},
        onItemClicked: (id: Long) -> Unit = {},
    ) {
        composeRule.setContent {
            val pagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
            LeafletTheme {
                PlantsListContent(
                    plants = pagingItems,
                    searchQuery = searchQuery,
                    showSavedOnly = showSavedOnly,
                    filters = filters,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onFiltersApplied = onFiltersApplied,
                    toggleShowSaved = toggleShowSaved,
                    onItemClicked = onItemClicked,
                )
            }
        }
    }

    private fun plants() = listOf(
        PlantsListItemUiData(1, "Monstera", "Monstera deliciosa", null),
        PlantsListItemUiData(2, "Snake Plant", "Dracaena trifasciata", null),
        PlantsListItemUiData(3, "Aloe Vera", null, null),
    )

    @Test
    fun `plant rows are displayed`() {
        setContent()

        composeRule.onNodeWithText("Monstera").assertIsDisplayed()
        composeRule.onNodeWithText("Snake Plant").assertIsDisplayed()
        composeRule.onNodeWithText("Aloe Vera").assertIsDisplayed()
    }

    @Test
    fun `clicking a row emits its id`() {
        var clickedId: Long? = null
        setContent(onItemClicked = { clickedId = it })

        composeRule.onNodeWithText("Snake Plant").performClick()

        assertEquals(2L, clickedId)
    }

    @Test
    fun `saved chip is off when not filtering and on when filtering`() {
        setContent(showSavedOnly = false)
        composeRule.onNodeWithText(string(R.string.saved)).assertIsOff()
    }

    @Test
    fun `saved chip is on when filtering saved`() {
        setContent(showSavedOnly = true)
        composeRule.onNodeWithText(string(R.string.saved)).assertIsOn()
    }

    @Test
    fun `clicking saved chip toggles the filter`() {
        var toggled = false
        setContent(toggleShowSaved = { toggled = true })

        composeRule.onNodeWithText(string(R.string.saved)).performClick()

        assertTrue(toggled)
    }

    @Test
    fun `typing in search emits the query`() {
        var query: String? = null
        setContent(onSearchQueryChanged = { query = it })

        composeRule.onNodeWithText(string(R.string.search_plants)).performTextInput("mon")

        assertEquals("mon", query)
    }

    @Test
    fun `filter button opens the filter sheet`() {
        setContent()

        composeRule.onNodeWithContentDescription(string(R.string.filter)).performClick()

        composeRule.onNodeWithText(string(R.string.filters)).assertIsDisplayed()
    }

    @Test
    fun `applying filters emits the selection and closes the sheet`() {
        var applied: PlantFilters? = null
        setContent(onFiltersApplied = { applied = it })
        composeRule.onNodeWithContentDescription(string(R.string.filter)).performClick()

        composeRule.onNodeWithText(string(R.string.filter_safety_pet_safe)).performClick()
        composeRule.onNodeWithText(string(R.string.apply)).performClick()
        composeRule.waitForIdle()

        assertEquals(setOf(SafetyFilter.PetSafe), applied?.safety)
        composeRule.onNodeWithText(string(R.string.filters)).assertDoesNotExist()
    }
}
