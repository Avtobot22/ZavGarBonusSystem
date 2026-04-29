import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    alias(libs.plugins.zavgar.parcelable)
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

    android {
        namespace = "com.zavgar.system.appstate"
    }
}
