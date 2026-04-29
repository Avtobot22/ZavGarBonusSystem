import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
    id("kotlin-parcelize")
}

kotlin {
    configureTargets("parcelable")

    android {
        namespace = "com.zavgar.system.parcelable"
    }
}
