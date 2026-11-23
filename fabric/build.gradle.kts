plugins {
    alias(libs.plugins.fabric.loom)
}

dependencies {

    implementation(project(":common"))

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
