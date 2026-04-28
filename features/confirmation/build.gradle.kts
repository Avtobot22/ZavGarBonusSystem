import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
    alias(libs.plugins.escodro.kotlin.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("confirmation")

    sourceSets {

        commonMain.dependencies {

            implementation(projects.core)

            implementation(projects.domain)
            implementation(projects.resources)
            implementation(projects.utils.validation)
            implementation(projects.libraries.navigationContracts)
            implementation(projects.libraries.coroutines)
            implementation(projects.libraries.designsystem)
            implementation(projects.libraries.parcelable)

            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.logging)
        }
    }

    android {
        namespace = "com.zavgar.system.confirmation"
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.compose.uiTooling)
}
