import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    alias(libs.plugins.zavgar.parcelable)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    configureTargets("shared")

    sourceSets {

        commonMain.dependencies {
            implementation(projects.data.datastore)
            implementation(projects.data.network)

            implementation(projects.domain.session)
            implementation(projects.utils.result)
            implementation(projects.utils.validation)

            implementation(projects.libraries.navigationContracts)
            implementation(projects.features.registration)
            implementation(projects.features.authorization)
            implementation(projects.features.confirmation)
            implementation(projects.features.resetpassword)
            implementation(projects.features.navigation)
            implementation(projects.features.home)
            implementation(projects.features.splash)
            implementation(projects.features.wallet)
            implementation(projects.features.settings)
            implementation(projects.features.account)
            implementation(projects.features.history)

            implementation(projects.libraries.coroutines)
            implementation(projects.libraries.designsystem)
            implementation(projects.libraries.appstate)
            implementation(projects.libraries.parcelable)
            implementation(projects.libraries.events)

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

    android {
        namespace = "com.zavgar.system.shared"
    }
}
