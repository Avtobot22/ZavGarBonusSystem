import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("operations")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }

    android {
        namespace = "com.zavgar.system.domain.operations"
    }
}
