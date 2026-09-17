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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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

val localReposScript = file("local.repositories.gradle.kts")
if (localReposScript.exists()) {
    apply(from = localReposScript)
}

rootProject.name = "Commerce SDK Demo App"
include(":app")

 