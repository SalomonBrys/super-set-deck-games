plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val createGameJson = tasks.create<CreateGamesJsonTask>("createGameJson")
val asciidoctor = tasks.create<AsciidoctorTask>("asciidoctor")

val copyPngs = tasks.create<Copy>("copyPngs") {
    group = "build"
    from(projectDir.resolve("games")) {
        include("**/R-*.png")
    }
    into(buildDir.resolve("generatedResources/games"))
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
            dependsOn(createGameJson, asciidoctor, copyPngs)
            from(buildDir.resolve("generatedResources"))
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
