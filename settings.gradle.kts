rootProject.name = "ZavGarBonusSystem"

include(":app")

include(":core")

include(":data:datastore")
include(":data:network")
include(":data:network-mock")
include(":data:repository")

include(":domain:session")
include(":domain:userinfo")
include(":domain:auth")
include(":domain:operations")
include(":domain:theme")
include(":domain:onboarding")

include(":features:authorization")
include(":features:registration")
include(":features:confirmation")
include(":features:navigation")
include(":features:home")
include(":features:splash")
include(":features:onboarding")
include(":features:wallet")
include(":features:settings")
include(":features:account")
include(":features:history")

include(":libraries:appstate")
include(":libraries:firebase")
include(":libraries:parcelable")
include(":libraries:coroutines")
include(":libraries:designsystem")
include(":libraries:events")
include(":libraries:navigation-contracts")
include(":libraries:analytics-api")

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
