import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("utils-validation")

    sourceSets {

        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.domain)
            implementation(projects.resources)

            implementation(libs.compose.runtime)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)

            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)
        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.utils.validation"
    }
}
