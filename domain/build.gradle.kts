import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("domain")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.logging)
            implementation(libs.kotlinx.datetime)
        }

    }

    android {
        namespace = "com.zavgar.system.domain"
    }
}
