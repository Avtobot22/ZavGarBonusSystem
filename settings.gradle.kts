include(":app")

include(":core")

include(":data:datastore")
include(":data:network")

include(":domain:session")
include(":domain:userinfo")

include(":features:authorization")
include(":features:registration")
include(":features:confirmation")
include(":features:resetpassword")
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
include(":libraries:events")
include(":libraries:navigation-contracts")

include(":shared")
include(":utils:validation")
include(":utils:result")
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
