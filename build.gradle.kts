plugins {
    kotlin("multiplatform") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

tasks.create<CreateGamesJsonTask>("createGamesJson")
tasks.create<AsciidoctorTask>("asciidoctor")

task<Sync>("publish") {
    group = "publish"
    dependsOn(":App:jsBrowserDistribution")
    from("$rootDir/App/build/distributions")
    into("$rootDir/docs")
}
