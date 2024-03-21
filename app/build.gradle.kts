plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

group = "tempopod"
version = "0.0.1"

kotlin {
    jvm() {
        mainRun {
            mainClass = "dev.niltsiar.tempopod.TempoPod"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
    }
}
