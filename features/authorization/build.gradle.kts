import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    alias(libs.plugins.zavgar.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("authorization")

    sourceSets {

        commonMain.dependencies {

            implementation(projects.core)

            implementation(projects.domain.auth)
            implementation(projects.utils.result)
            implementation(projects.resources)
            implementation(projects.utils.validation)
            implementation(projects.libraries.navigationContracts)
            implementation(projects.libraries.coroutines)
            implementation(projects.libraries.designsystem)
            implementation(projects.libraries.firebase)
            implementation(projects.libraries.parcelable)

            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.compose)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.logging)
        }
    }

    android {
        namespace = "com.zavgar.system.authorization"
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.compose.uiTooling)
}
