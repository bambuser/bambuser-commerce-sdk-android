pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Adding the bambuser commerce sdk maven here
        maven {
            url = uri("https://repo.repsy.io/mvn/bambuser/bambuser-commerce-sdk")
        }
    }
}

rootProject.name = "Commerce SDK Demo App"
include(":app")

 