plugins {
    id("com.gradleup.shadow") version "9.6.1"
}

dependencies {

    implementation(project(":common"))

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifact(tasks.shadowJar)

            groupId = "com.g4vrk"
            artifactId = "schedula-bukkit"
            version = project.version.toString()
        }
    }
}