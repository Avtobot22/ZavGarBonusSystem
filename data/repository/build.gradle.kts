import extension.configureTargets

plugins {
    alias(libs.plugins.zavgar.multiplatform)
}

kotlin {
    configureTargets("repository")

    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.network)
            implementation(projects.data.datastore)
            implementation(projects.domain.auth)
            implementation(projects.domain.session)
            implementation(projects.domain.operations)
            implementation(projects.domain.userinfo)
            implementation(projects.domain.theme)
            implementation(projects.domain.onboarding)
            implementation(projects.utils.result)
            implementation(projects.libraries.coroutines)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }

    android {
        namespace = "com.zavgar.system.data.repository"
    }
}
