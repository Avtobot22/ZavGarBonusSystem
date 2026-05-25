import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("session")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(projects.libraries.events)
            implementation(projects.libraries.coroutines)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }

    android {
        namespace = "com.zavgar.system.domain.session"
    }
}
