import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
    alias(libs.plugins.escodro.kotlin.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("appstate")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.libraries.parcelable)
            implementation(projects.libraries.navigationContracts)

            implementation(libs.compose.runtime)
            implementation(libs.compose.navigation.ui)
        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.appstate"
    }
}
