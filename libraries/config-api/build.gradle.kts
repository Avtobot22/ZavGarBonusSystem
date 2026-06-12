import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("config-api")

    sourceSets {
        commonMain.dependencies {
        }
    }

    android {
        namespace = "com.zavgar.system.config"
    }
}
