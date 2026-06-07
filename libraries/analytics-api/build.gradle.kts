import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("analytics-api")

    sourceSets {
        commonMain.dependencies {
        }
    }

    android {
        namespace = "com.zavgar.system.analytics"
    }
}
