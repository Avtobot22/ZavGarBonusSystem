include(":app")

include(":core")

include(":data:repository")
include(":data:datastore")
include(":data:network")

include(":domain")

include(":features:authorization")
include(":features:registration")
include(":features:confirmation")
include(":features:resetpassword")
include(":features:navigation-api")
include(":features:navigation")
include(":features:home")
include(":features:splash")
include(":features:wallet")
include(":features:settings")
include(":features:account")
include(":features:history")

include(":libraries:appstate")
include(":libraries:parcelable")
include(":libraries:coroutines")
include(":libraries:designsystem")

include(":shared")
include(":shared-validation")
include(":resources")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("plugins")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
