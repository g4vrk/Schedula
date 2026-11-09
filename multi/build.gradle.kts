dependencies {

    implementation(project(":common"))
    implementation(project(":folia"))
    implementation(project(":bukkit"))

    compileOnly(libs.folia.api)

}

// Bundles the Folia flavour, which needs Java 21.
java {

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

}
