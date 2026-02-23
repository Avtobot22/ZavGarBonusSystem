import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
    alias(libs.plugins.escodro.kotlin.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("core")

    sourceSets {

        commonMain.dependencies {

            implementation(libs.compose.components.resources)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.core"
    }
}
