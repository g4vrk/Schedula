dependencies {

    compileOnly(libs.jetbrains.annotations)

}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.named<JavaCompile>("compileJava") {
    options.release.set(16)
}

tasks.named<JavaCompile>("compileTestJava") {
    options.release.set(17)
}
