package dev.notsatria.stop_pmo.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Dashboard : Screen()
    @Serializable
    object Analytics : Screen()
    @Serializable
    object Settings : Screen()
    @Serializable
    object History : Screen()
}