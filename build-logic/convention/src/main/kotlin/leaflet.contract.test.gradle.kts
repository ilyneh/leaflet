/**
 * Adds a `contractTest` source set and task for tests that hit live third-party APIs.
 *
 * - Sources: `src/contractTest/kotlin`, resources: `src/contractTest/resources`
 * - Inherits `testImplementation` dependencies
 * - Never wired into `check`; run explicitly:
 *     ./gradlew :core:network:plants:contractTest
 * - Reads PERENUAL_API_KEY from local.properties, then the environment, like :app
 */
import java.util.Properties

plugins {
    kotlin("jvm")
}

val contractTest: SourceSet = sourceSets.create("contractTest") {
    compileClasspath += sourceSets["main"].output
    runtimeClasspath += output + compileClasspath
}

// contractTest is a custom source set, so unlike `test` it can't see main's internal classes by default.
kotlin.target.compilations.named("contractTest") {
    associateWith(kotlin.target.compilations.getByName("main"))
}

configurations["contractTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["contractTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

tasks.register<Test>("contractTest") {
    description = "Runs contract tests against live external APIs. Not part of `check`."
    group = "verification"
    testClassesDirs = contractTest.output.classesDirs
    classpath = contractTest.runtimeClasspath
    shouldRunAfter("test")
    outputs.upToDateWhen { false }

    testLogging { events("passed", "skipped", "failed") }

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
    val key = localProperties.getProperty("PERENUAL_API_KEY")
        ?: providers.environmentVariable("PERENUAL_API_KEY").orNull
        ?: ""
    environment("PERENUAL_API_KEY", key)
}
