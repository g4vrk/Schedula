dependencies {

    implementation(project(":common"))
    implementation(project(":folia"))
    implementation(project(":bukkit"))

    compileOnly(libs.folia.api)

}