dependencies {

    implementation(project(":common"))

    compileOnly(libs.folia.api)

}

// Folia itself runs on Java 21 only.
java {

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

}
