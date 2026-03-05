import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    configureTargets("resources")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.components.resources)
            implementation(libs.koin.core)
        }
    }

    android {
        namespace = "com.zavgar.system.resources"
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.zavgar.system.resources"
    generateResClass = always
}
