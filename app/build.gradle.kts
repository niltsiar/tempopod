plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.niltsiar.tempopod"
version = "0.0.1"

kotlin {
    jvm() {
        mainRun {
            mainClass = "dev.niltsiar.tempopod.MainKt"
        }
    }
    linuxX64() {
        binaries {
            executable { entryPoint = "dev.niltsiar.tempopod.main" }
        }
    }
    macosX64() {
        binaries {
            executable { entryPoint = "dev.niltsiar.tempopod.main" }
        }
    }
    macosArm64() {
        binaries {
            executable { entryPoint = "dev.niltsiar.tempopod.main" }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
    }
}
