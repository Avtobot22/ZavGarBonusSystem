import extension.configureTargets

plugins {
    alias(libs.plugins.escodro.multiplatform)
}

kotlin {
    configureTargets("datastore")

    sourceSets {
        commonMain.dependencies {
            api(projects.utils.result)
            implementation(projects.libraries.coroutines)

            implementation(libs.koin.core)
            implementation(libs.androidx.datastore)
        }
    }

    android {
        namespace = "com.zavgar.system.datastore"
    }
}
