dependencies {

    implementation(project(":common"))

    compileOnly(libs.sponge.api)
    compileOnly(libs.jetbrains.annotations)

}

java {

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

}

repositories {

    maven {
        url = uri("https://repo.spongepowered.org/maven/")
    }

}
