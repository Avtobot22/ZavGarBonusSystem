import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("datastore")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.repository)
            implementation(projects.libraries.coroutines)

            implementation(libs.koin.core)
            implementation(libs.androidx.datastore)
        }
    }

    androidLibrary {
        namespace = "com.zavgar.system.datastore"
    }
}
