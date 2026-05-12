import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    alias(libs.plugins.zavgar.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("core")

    sourceSets {

        commonMain.dependencies {

            implementation(libs.compose.components.resources)
            implementation(projects.resources)
            implementation(projects.utils.result)
            implementation(libs.compose.material3)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

        }
    }

    android {
        namespace = "com.zavgar.system.core"
    }
}
