import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("session")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(projects.data.datastore)
            implementation(projects.libraries.events)
            implementation(projects.libraries.coroutines)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.domain.session"
    }
}
