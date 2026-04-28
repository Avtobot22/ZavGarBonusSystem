import java.util.Properties

plugins {
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
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
            keyAlias = getSigningKey(properties, "ZAVGAR_KEY_ALIAS", "keyAlias")
            keyPassword = getSigningKey(properties, "ZAVGAR_KEY_PASSWORD", "keyPassword")
            storeFile = file(getSigningKey(properties, "ZAVGAR_STORE_PATH", "storePath"))
            storePassword = getSigningKey(properties, "ZAVGAR_KEY_STORE_PASSWORD", "storePassword")
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
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
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

    implementation(libs.logcat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.material)
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}

fun getSigningKey(properties: Properties, secretKey: String, propertyKey: String): String =
    if (!System.getenv(secretKey).isNullOrEmpty()) {
        System.getenv(secretKey)
    } else {
        properties.getProperty(propertyKey)
    }
