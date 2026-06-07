plugins {
    alias(libs.plugins.android.kmp.plugin) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kover)
}
buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

project(":app").pluginManager.apply("org.jetbrains.kotlinx.kover")

subprojects {
    val subproject = this
    subproject.pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
        rootProject.dependencies.add("kover", subproject)
    }
}
