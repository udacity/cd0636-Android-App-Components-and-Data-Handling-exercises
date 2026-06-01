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
    }
}

rootProject.name = "cd0636-Android-App-Components-and-Data-Handling"

// Building with ViewModel
include(":building-with-viewmodel:building-with-viewmodel-starter")
include(":building-with-viewmodel:solution")
include(":building-with-viewmodel:demo")

// Observables and UI States
include(":observables-and-ui-states:observables-and-ui-states-starter:livedata")
include(":observables-and-ui-states:solution:livedata")
include(":observables-and-ui-states:demo")

// Saving and Restoring UI States
include(":saving-and-restoring-ui-states:saving-and-restoring-ui-states-starter")
include(":saving-and-restoring-ui-states:solution")
include(":saving-and-restoring-ui-states:demo")

// Building Navigation Graphs
include(":building-navigation-graphs:building-navigation-graphs-starter")
include(":building-navigation-graphs:solution")
include(":building-navigation-graphs:demo")

// Advanced Navigation Patterns
include(":advanced-navigation-patterns:advanced-navigation-patterns-starter")
include(":advanced-navigation-patterns:solution")
include(":advanced-navigation-patterns:demo")

// Implementing Authentication
include(":implementing-authentication:implementing-authentication-starter")
include(":implementing-authentication:solution")
include(":implementing-authentication:demo")

// Kotlin Coroutines
include(":kotlin-coroutines:kotlin-coroutines-starter")
include(":kotlin-coroutines:solution")
include(":kotlin-coroutines:demo")

// Setting up Retrofit
include(":setting-up-retrofit:setting-up-retrofit-starter")
include(":setting-up-retrofit:solution")
include(":setting-up-retrofit:demo")

// Parsing JSON Data
include(":parsing-json-data:parsing-json-data-starter")
include(":parsing-json-data:solution")
include(":parsing-json-data:demo")

// Building a Repository
include(":building-a-repository:building-a-repository-starter")
include(":building-a-repository:solution")
include(":building-a-repository:demo")

// Room Database Setup
include(":room-database-setup:room-database-setup-starter")
include(":room-database-setup:solution")
include(":room-database-setup:demo")

// Room Queries and Operations
include(":room-queries-and-operations:room-queries-and-operations-starter")
include(":room-queries-and-operations:solution")
include(":room-queries-and-operations:demo")

// RecyclerView Deep Dive
include(":recycler-view-deep-dive:recycler-view-deep-dive-starter")
include(":recycler-view-deep-dive:solution")
include(":recycler-view-deep-dive:demo")

// Building Lists in Compose
include(":building-lists-in-compose:building-lists-in-compose-starter")
include(":building-lists-in-compose:solution")
include(":building-lists-in-compose:demo")

// Implementing Form Validation
include(":implementing-form-validation:implementing-form-validation-starter")
include(":implementing-form-validation:solution")
include(":implementing-form-validation:demo")

// Error Handling and Validation
include(":error-handling-and-validation:error-handling-and-validation-starter")
include(":error-handling-and-validation:solution")
include(":error-handling-and-validation:demo")

// Testing the Data Layer
include(":testing-the-data-layer:testing-the-data-layer-starter")
include(":testing-the-data-layer:solution")
include(":testing-the-data-layer:demo")
