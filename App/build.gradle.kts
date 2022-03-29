import org.gradle.api.internal.file.copy.CopySpecInternal

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.1.0"
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

                val materialWebComponentsVersion = "13.0.0"
                implementation(npm("@material/button", materialWebComponentsVersion))
                implementation(npm("@material/card", materialWebComponentsVersion))
                implementation(npm("@material/checkbox", materialWebComponentsVersion))
                implementation(npm("@material/chips", materialWebComponentsVersion))
                implementation(npm("@material/dialog", materialWebComponentsVersion))
                implementation(npm("@material/drawer", materialWebComponentsVersion))
                implementation(npm("@material/form-field", materialWebComponentsVersion))
                implementation(npm("@material/icon-button", materialWebComponentsVersion))
                implementation(npm("@material/list", materialWebComponentsVersion))
                implementation(npm("@material/menu", materialWebComponentsVersion))
                implementation(npm("@material/menu-surface", materialWebComponentsVersion))
                implementation(npm("@material/ripple", materialWebComponentsVersion))
                implementation(npm("@material/select", materialWebComponentsVersion))
                implementation(npm("@material/tab-bar", materialWebComponentsVersion))
                implementation(npm("@material/top-app-bar", materialWebComponentsVersion))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

                implementation("app.softwork:routing-compose:0.1.8")

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
