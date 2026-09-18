import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import util.VersionUtility
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java-library")
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

version = VersionUtility.version(project, version.toString())

allprojects {

    repositories {

        mavenCentral()

        maven {
            name = "papermc-repo"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }

    }

}

subprojects {

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    group = rootProject.group
    version = rootProject.version
    description = rootProject.description

    dependencies {

        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)

    }

    plugins.withType<JavaPlugin> {

        dependencies {
            "testImplementation"(
                platform(rootProject.libs.junit.bom)
            )

            "testImplementation"(
                rootProject.libs.junit.jupiter
            )

            "testRuntimeOnly"(
                rootProject.libs.junit.platform.launcher
            )
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    extensions.configure<JavaPluginExtension> {

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()

    }


    tasks {

        withType<JavaCompile>().configureEach {

            options.encoding = "UTF-8"

        }

        withType<RunServer>().configureEach {
            minecraftVersion(libs.versions.minecraft.get())
            jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
        }

        withType<ShadowJar>().configureEach {
            archiveFileName.set(
                "${rootProject.name}-${project.name}-${rootProject.version}.jar"
            )

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            mergeServiceFiles()
        }

        named("build") {

            dependsOn(named("shadowJar"))

        }

    }


    extensions.configure<PublishingExtension> {

        publications {

            create<MavenPublication>("maven") {

                groupId = System.getenv("GROUP") ?: project.group.toString()
                artifactId = project.name
                version = System.getenv("VERSION") ?: project.version.toString()

                artifact(tasks.named<ShadowJar>("shadowJar")) {
                    classifier = ""
                }
                artifact(tasks.named<Jar>("sourcesJar"))

                pom {
                    name.set("${rootProject.name}-${project.name}")
                    description.set(provider { project.description })
                }

            }

        }

    }

}


tasks {

    named<Jar>("jar") {

        enabled = false

    }

    clean {

        dependsOn(subprojects.map { "${it.path}:clean" })

    }

    build {

        dependsOn(subprojects.map { "${it.path}:build" })

    }

}

defaultTasks("clean", "build")
