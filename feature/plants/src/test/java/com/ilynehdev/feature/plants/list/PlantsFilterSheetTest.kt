package com.ilynehdev.feature.plants.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.data.plants.filters.LightFilter
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.data.plants.filters.WateringFilter
import com.ilynehdev.feature.plants.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantsFilterSheetTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(resId: Int) = composeRule.activity.getString(resId)

    private var applied: PlantFilters? = null
    private var dismissed = false

    private fun setContent(current: PlantsListFiltersUiData = PlantsListFiltersUiData()) {
        composeRule.setContent {
            LeafletTheme {
                PlantsFilterSheet(
                    current = current,
                    onApply = { applied = it },
                    onDismiss = { dismissed = true },
                )
            }
        }
    }

    private fun chip(resId: Int) =
        composeRule.onNodeWithText(string(resId)).performScrollTo()

    @Test
    fun `draft is seeded from current selections`() {
        setContent(
            PlantsListFiltersUiData(
                selectedLight = setOf(LightFilter.LowLight),
                selectedSafety = setOf(SafetyFilter.PetSafe),
            )
        )

        chip(R.string.filter_light_low).assertIsOn()
        chip(R.string.filter_safety_pet_safe).assertIsOn()
        chip(R.string.filter_light_medium).assertIsOff()
        chip(R.string.filter_watering_weekly).assertIsOff()
    }

    @Test
    fun `toggling a selected chip off removes it from the applied filters`() {
        setContent(PlantsListFiltersUiData(selectedSafety = setOf(SafetyFilter.PetSafe)))

        chip(R.string.filter_safety_pet_safe).performClick()
        composeRule.onNodeWithText(string(R.string.apply)).performClick()
        composeRule.waitForIdle()

        assertEquals(emptySet<SafetyFilter>(), applied?.safety)
    }

    @Test
    fun `selections across sections are applied together`() {
        setContent()

        chip(R.string.filter_light_bright_indirect).performClick()
        chip(R.string.filter_watering_weekly).performClick()
        composeRule.onNodeWithText(string(R.string.apply)).performClick()
        composeRule.waitForIdle()

        assertEquals(setOf(LightFilter.BrightIndirect), applied?.light)
        assertEquals(setOf(WateringFilter.Weekly), applied?.watering)
    }

    @Test
    fun `reset applies empty filters`() {
        setContent(
            PlantsListFiltersUiData(
                selectedLight = setOf(LightFilter.LowLight),
                selectedSafety = setOf(SafetyFilter.PetSafe),
            )
        )

        composeRule.onNodeWithText(string(R.string.reset)).performClick()
        composeRule.waitForIdle()

        assertEquals(PlantFilters(), applied)
    }

    @Test
    fun `cancel dismisses without applying`() {
        setContent(PlantsListFiltersUiData(selectedLight = setOf(LightFilter.LowLight)))

        composeRule.onNodeWithText(string(R.string.cancel)).performClick()
        composeRule.waitForIdle()

        assertTrue(dismissed)
        assertNull(applied)
    }
}
