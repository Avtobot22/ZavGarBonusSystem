import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("repository")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.libraries.coroutines)
            implementation(projects.domain)

            implementation(libs.ktor.client.core)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.repository"
    }
}
