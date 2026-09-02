import com.ilynehdev.leaflet.buildlogic.convention.libs

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
}