import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

plugins {
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    defaultConfig {
        applicationId = "com.zavgar.system.bonusapp"
        versionCode = Integer.parseInt(libs.versions.version.code.get())
        versionName = libs.versions.version.name.get()
        compileSdk = Integer.parseInt(libs.versions.android.sdk.compile.get())
        minSdk = Integer.parseInt(libs.versions.android.sdk.min.get())
        targetSdk = Integer.parseInt(libs.versions.android.sdk.target.get())
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        base.archivesName.set("${parent?.name}-$versionName")
    }

    val properties = readProperties(file("../config/signing/signing.properties"))
    signingConfigs {
        create("release") {
            // Release signing is wired only when credentials are available — either the
            // (git-ignored) signing.properties or the ZAVGAR_* env vars. CI builds debug only,
            // so a missing signing config must not break project configuration.
            val storePath = getSigningKey(properties, "ZAVGAR_STORE_PATH", "storePath")
            if (storePath != null) {
                keyAlias = getSigningKey(properties, "ZAVGAR_KEY_ALIAS", "keyAlias")
                keyPassword = getSigningKey(properties, "ZAVGAR_KEY_PASSWORD", "keyPassword")
                storeFile = file(storePath)
                storePassword = getSigningKey(properties, "ZAVGAR_KEY_STORE_PASSWORD", "storePassword")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }

        getByName("debug") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-DEV"
            isMinifyEnabled = false

            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
    }

    // Переключение между реальным бэкендом и in-memory Ktor mock сделано через product
    // flavor (измерение "server"), а не через Gradle-property. В Android Studio варианты
    // mockDebug / prodDebug выбираются в Build Variants и запускаются кнопкой Run без
    // правки аргументов сборки. mock получает свой applicationIdSuffix, поэтому mock- и
    // prod-сборки уживаются на устройстве одновременно.
    flavorDimensions += "server"
    productFlavors {
        create("prod") {
            dimension = "server"
            // Реальный бэкенд: mock-граф в Koin не подмешивается.
            buildConfigField("boolean", "USE_MOCK_SERVER", "false")
        }

        create("mock") {
            dimension = "server"
            // Offline-режим: сетевой слой работает на заготовленных ответах без бэкенда.
            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"
            buildConfigField("boolean", "USE_MOCK_SERVER", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
            add("META-INF/versions/9/previous-compilation-data.bin")
        }
    }

    namespace = "com.zavgar.system.app"
}

dependencies {
    implementation(projects.shared)
    implementation(projects.libraries.navigationContracts)

    // Нативный Firebase SDK подключается транзитивно через :libraries:firebase (GitLive).
    // Плагины google-services и firebase-crashlytics остаются для обработки
    // google-services.json и выгрузки mapping-файлов.
    implementation(libs.logcat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.material)
}

fun readProperties(propertiesFile: File) = Properties().apply {
    // Optional on CI: the file is git-ignored, so absence must not fail configuration.
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { fis ->
            load(fis)
        }
    }
}

fun getSigningKey(properties: Properties, secretKey: String, propertyKey: String): String? =
    System.getenv(secretKey)?.takeIf { it.isNotEmpty() } ?: properties.getProperty(propertyKey)
