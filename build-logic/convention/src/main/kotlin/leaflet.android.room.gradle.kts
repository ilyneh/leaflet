import com.ilynehdev.leaflet.buildlogic.convention.libs

/**
 * Room setup for Android modules: KSP + Room Gradle plugin, schema export
 * to `<module>/schemas`, runtime and compiler dependencies.
 */
plugins {
    id("com.google.devtools.ksp")
    id("androidx.room3")
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    "implementation"(libs.findLibrary("androidx.room.runtime").get())
    "ksp"(libs.findLibrary("androidx.room.compiler").get())
}
