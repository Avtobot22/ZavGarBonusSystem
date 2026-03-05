plugins {
    id("com.escodro.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    kotlin("plugin.parcelize")
}

kotlin {
    android {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.zavgar.system.parcelable.CommonParcelize",
            )
        }
    }
}
