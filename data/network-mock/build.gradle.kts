import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("networkMock")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.network)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.mock)

            implementation(libs.koin.core)
        }
    }

    android {
        namespace = "com.zavgar.system.networkmock"
    }
}
