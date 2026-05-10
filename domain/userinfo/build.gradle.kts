import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("userinfo")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(projects.data.network)
            implementation(projects.libraries.coroutines)

            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }

    android {
        namespace = "com.zavgar.system.domain.userinfo"
    }
}
