dependencies {

    implementation(project(":common"))

    compileOnly(libs.minestom)
    compileOnly(libs.jetbrains.annotations)

}

java {

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

}
