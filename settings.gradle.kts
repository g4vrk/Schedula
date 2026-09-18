rootProject.name = "schedula"

include(
    "common",
    "bukkit",
    "folia",
    "multi"
)

dependencyResolutionManagement {

    versionCatalogs {

        create("libs") {

            from(files("libs.versions.toml"))

        }

    }

}