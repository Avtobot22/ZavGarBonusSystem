import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("utils-validation")

    sourceSets {

        commonMain.dependencies {
            implementation(projects.utils.result)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
        }
    }

    android {
        namespace = "com.zavgar.system.utils.validation"
    }
}
