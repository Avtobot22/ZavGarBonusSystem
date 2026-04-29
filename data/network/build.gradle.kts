import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    configureTargets("network")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.data.datastore)
            implementation(projects.domain.session)
            implementation(projects.libraries.events)
            implementation(projects.libraries.coroutines)
            implementation(projects.utils.result)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    android {
        namespace = "com.zavgar.system.network"
    }
}
