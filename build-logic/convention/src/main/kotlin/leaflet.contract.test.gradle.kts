/**
 * Adds a `contractTest` source set and task for tests that hit live third-party APIs.
 *
 * - Sources: `src/contractTest/kotlin`, resources: `src/contractTest/resources`
 * - Inherits `testImplementation` dependencies
 * - Never wired into `check`; run explicitly:
 *     PLANTS_API_KEY=... ./gradlew :core:network:plants:contractTest
 */
plugins {
    kotlin("jvm")
}

val contractTest: SourceSet = sourceSets.create("contractTest") {
    compileClasspath += sourceSets["main"].output
    runtimeClasspath += output + compileClasspath
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

    val key = providers.environmentVariable("PLANTS_API_KEY")
        .orElse(providers.gradleProperty("plantsApiKey"))
        .getOrElse("")
    environment("PLANTS_API_KEY", key)
}
