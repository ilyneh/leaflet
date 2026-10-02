package com.ilynehdev.feature.plants.presentation.list

import androidx.annotation.StringRes
import com.ilynehdev.feature.plants.data.filters.CareLevelFilter
import com.ilynehdev.feature.plants.data.filters.LightFilter
import com.ilynehdev.feature.plants.data.filters.MatureSizeFilter
import com.ilynehdev.feature.plants.data.filters.SafetyFilter
import com.ilynehdev.feature.plants.data.filters.WateringFilter
import com.ilynehdev.feature.plants.presentation.R

@StringRes
internal fun LightFilter.labelRes(): Int = when (this) {
    LightFilter.LowLight -> R.string.filter_light_low
    LightFilter.Medium -> R.string.filter_light_medium
    LightFilter.BrightIndirect -> R.string.filter_light_bright_indirect
    LightFilter.DirectSun -> R.string.filter_light_direct_sun
}

@StringRes
internal fun WateringFilter.labelRes(): Int = when (this) {
    WateringFilter.Weekly -> R.string.filter_watering_weekly
    WateringFilter.EveryTwoWeeks -> R.string.filter_watering_every_two_weeks
    WateringFilter.Monthly -> R.string.filter_watering_monthly
}

@StringRes
internal fun SafetyFilter.labelRes(): Int = when (this) {
    SafetyFilter.PetSafe -> R.string.filter_safety_pet_safe
    SafetyFilter.NonToxicToChildren -> R.string.filter_safety_child_safe
}

@StringRes
internal fun CareLevelFilter.labelRes(): Int = when (this) {
    CareLevelFilter.Easy -> R.string.filter_care_easy
    CareLevelFilter.Moderate -> R.string.filter_care_moderate
    CareLevelFilter.Fussy -> R.string.filter_care_fussy
}

@StringRes
internal fun MatureSizeFilter.labelRes(): Int = when (this) {
    MatureSizeFilter.Tabletop -> R.string.filter_size_tabletop
    MatureSizeFilter.Shelf -> R.string.filter_size_shelf
    MatureSizeFilter.Floor -> R.string.filter_size_floor
    MatureSizeFilter.Tall -> R.string.filter_size_tall
}
