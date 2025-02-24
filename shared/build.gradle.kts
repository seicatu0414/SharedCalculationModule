import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

// M1/M2 Mac のシミュレーターを使う場合は `true`
val enableM1Simulator = true

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOfNotNull(
        iosX64().takeIf { !enableM1Simulator },
        iosArm64(),
        iosSimulatorArm64().takeIf { enableM1Simulator }
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
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
                implementation(kotlin("test"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.junit.v115)
                implementation(libs.androidx.espresso.core)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting

        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

// ✅ `assembleXCFramework` を有効化
tasks.register("assembleXCFramework") {
    dependsOn("linkReleaseFrameworkIosArm64",
        "linkReleaseFrameworkIosSimulatorArm64")
    if (!enableM1Simulator) {
        dependsOn("linkReleaseFrameworkIosX64")
    }
    doLast {
        val outputDir = project.layout.buildDirectory.get().asFile.resolve("XCFrameworks")
        outputDir.mkdirs()

        val command = mutableListOf(
            "xcodebuild",
            "-create-xcframework",
            "-framework", project.layout.buildDirectory.get().asFile.resolve("bin/iosArm64/releaseFramework/Shared.framework").absolutePath,
            "-framework", project.layout.buildDirectory.get().asFile.resolve("bin/iosSimulatorArm64/releaseFramework/Shared.framework").absolutePath
        )

        if (!enableM1Simulator) {
            command.add("-framework")
            command.add(project.layout.buildDirectory.get().asFile.resolve("bin/iosX64/releaseFramework/Shared.framework").absolutePath)
        }

        command.add("-output")
        command.add(outputDir.resolve("Shared.xcframework").absolutePath)

        exec {
            commandLine(command)
        }
    }
}

android {
    namespace = "org.shared_calculation_module.project.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    implementation(kotlin("test"))
}
