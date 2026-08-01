dependencies {

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}