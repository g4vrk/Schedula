import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.fabric.loom)
}

val shadowedCommon = configurations.create("shadowedCommon") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {

    implementation(project(":common"))
    add(shadowedCommon.name, project(":common"))

    minecraft("com.mojang:minecraft:${libs.versions.fabric.minecraft.get()}")
    compileOnly(libs.fabric.api)
    compileOnly(libs.jetbrains.annotations)

}

java {

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

}

repositories {

    maven {
        url = uri("https://maven.fabricmc.net/")
    }

}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shadowedCommon)
}
