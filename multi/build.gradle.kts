dependencies {

    implementation(project(":common"))
    implementation(project(":platform:folia"))
    implementation(project(":platform:bukkit"))

    compileOnly(libs.folia.api)

}

// Bundles the Folia flavour, which needs Java 21.
java {

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

}
