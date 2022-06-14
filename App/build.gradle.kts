import org.gradle.api.internal.file.copy.CopySpecInternal

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev709"
}

val copyAllResources by tasks.creating {
    group = "build"
}

val copyPngs = tasks.create<Copy>("copyPngs") {
    copyAllResources.dependsOn(this)
    group = "resources"
    from(rootDir.resolve("games")) {
        include("**/R-*.png")
    }
    into(buildDir.resolve("generatedResources/games"))
}

val copyGamesJson = tasks.create<Copy>("copyGamesJson") {
    copyAllResources.dependsOn(this)
    group = "resources"
    dependsOn(":createGamesJson")
    from(rootDir.resolve("build/games-json/games.json"))
    into(buildDir.resolve("generatedResources/games"))
}

val copyAsciidoctor = tasks.create<Copy>("copyAsciidoctor") {
    copyAllResources.dependsOn(this)
    group = "resources"
    dependsOn(":asciidoctor")
    from(rootDir.resolve("build/asciidoctor"))
    into(buildDir.resolve("generatedResources/games"))
}

val copyServiceWorker = tasks.create<Copy>("copyServiceWorker") {
    copyAllResources.dependsOn(this)
    dependsOn(":ServiceWorker:jsBrowserProductionWebpack")
    group = "resources"
    from(rootDir.resolve("ServiceWorker/build/distributions"))
    into(buildDir.resolve("generatedResources"))
}

val generateResourceList = tasks.create<CreateResourceListTask>("generateResourceList") {
    dependsOn("copyAllResources")
    dirs += file("$projectDir/src/jsMain/resources")
    dirs += file("$buildDir/generatedResources")
}

kotlin {
    js(IR) {
        useCommonJs()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }

        @Suppress("UnstableApiUsage")
        (tasks[compilations["main"].processResourcesTaskName] as ProcessResources).apply {
            dependsOn(copyAllResources, generateResourceList)
            from(buildDir.resolve("generatedResources"))
            from(buildDir.resolve("resourcesList"))
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)

                implementation("dev.petuska:kmdc:0.0.4")
                implementation("dev.petuska:kmdcx:0.0.4")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

                implementation("app.softwork:routing-compose:0.2.3")

                implementation(devNpm("sass-loader", "^13.0.0"))
                implementation(devNpm("sass", "^1.52.1"))

                implementation(npm("@uriopass/nosleep.js", "0.12.1"))
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}
