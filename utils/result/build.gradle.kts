import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("utils-result")

    sourceSets {
        commonMain.dependencies {
        }
    }

    android {
        namespace = "com.zavgar.system.utils.result"
    }
}
