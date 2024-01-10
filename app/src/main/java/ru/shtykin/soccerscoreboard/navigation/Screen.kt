package ru.shtykin.bbs_mobile.navigation

sealed class Screen(
    val route: String
) {
    object Settings: Screen(ROUTE_SETTINGS)
    object Game: Screen(ROUTE_GAME)
    object Developer: Screen(ROUTE_DEVELOPER)
    object Bluetooth: Screen(ROUTE_BLUETOOTH)

    private companion object {
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_GAME = "game"
        const val ROUTE_DEVELOPER = "developer"
        const val ROUTE_BLUETOOTH = "bluetooth"
    }
}
