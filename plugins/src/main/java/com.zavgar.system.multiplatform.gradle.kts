import extension.sdkCompile
import extension.sdkMin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    // Test coverage for every KMP module; aggregated from the root via `kover(project(...))`.
    id("org.jetbrains.kotlinx.kover")
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
            // Aligned with the Android app module (Java 17). Keep KMP library modules
            // on the same JVM target to avoid bytecode mismatch across the project.
            jvmTarget.set(JvmTarget.JVM_17)
        }

        // Enables the JVM-based unit-test component (creates the `androidHostTest` source set).
        withHostTest { }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        // Multiplatform test dependencies live in `commonTest` so the shared tests also
        // compile/run on iOS targets. `androidHostTest` inherits them through the default
        // hierarchy template (commonTest -> androidHostTest), so JVM/MockK tests still see them.
        getByName("commonTest").dependencies {
            implementation(_libs.findLibrary("kotlin-test").get())
            implementation(_libs.findLibrary("turbine").get())
            implementation(_libs.findLibrary("kotlinx-coroutines-test").get())
        }

        // MockK is JVM-only; keep it on the Android host-test source set exclusively.
        getByName("androidHostTest").dependencies {
            implementation(_libs.findLibrary("mockk").get())
        }
    }
}

tasks.withType<Test> {
    // Prevent the task from failing when no tests are found for Android targets
    failOnNoDiscoveredTests = false
}
