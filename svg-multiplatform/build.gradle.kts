plugins {
    kotlin("multiplatform") version "1.8.10"
}

group = "llesha"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
                    useFirefox()
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
            }
        }
        val commonTest by getting {
            dependencies {
                //implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}
