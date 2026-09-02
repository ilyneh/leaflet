import com.ilynehdev.leaflet.buildlogic.convention.libs

dependencies {
    "implementation"(libs.findBundle("ktor").get())
    "testImplementation"(libs.findBundle("ktor.test").get())
}