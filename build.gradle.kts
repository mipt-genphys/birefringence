plugins {
    kotlin("multiplatform") version "1.3.72"
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
}


group = "ru.mipt.physics"
version = "1.0"


application {
    mainClassName = "ru.mipt.physics.biref.BirefFXApp"
}

repositories {
    jcenter()
}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js{
        browser{
            webpackTask {
                group = "distribution"
            }
            distribution {
                directory = buildDir.resolve("installJS")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jfree:jfreechart-fx:1.0.0")
                implementation("no.tornado:tornadofx:1.7.19")
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
                implementation(npm("file-saver"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

javafx{
    modules("javafx.controls","javafx.web","javafx.fxml")
}
