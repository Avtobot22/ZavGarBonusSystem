import extension.configureTargets
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("firebase")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.gitlive.firebase.config)
            implementation(libs.gitlive.firebase.crashlytics)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
        }
    }

    android {
        namespace = "com.zavgar.system.firebase"
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
