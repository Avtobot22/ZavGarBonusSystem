import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("auth")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(libs.kotlinx.datetime)
            implementation(projects.libraries.coroutines)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }

    android {
        namespace = "com.zavgar.system.domain.auth"
    }
}
