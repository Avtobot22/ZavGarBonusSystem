import extension.sdkCompile
import extension.sdkMin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

private val _libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()

    android {
        compileSdk = Integer.parseInt(_libs.sdkCompile)
        minSdk = Integer.parseInt(_libs.sdkMin)

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

tasks.withType<Test> {
    // Prevent the task from failing when no tests are found for Android targets
    failOnNoDiscoveredTests = false
}
