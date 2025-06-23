package lt.vitalijus.records.app.navigation

import kotlinx.serialization.Serializable

interface NavigationRoute {

    @Serializable
    data class Records(
        val startRecording: Boolean
    ) : NavigationRoute

    @Serializable
    data class CreateRecord(
        val recordingPath: String,
        val duration: Long,
        val amplitudes: String
    ) : NavigationRoute

    @Serializable
    data object Settings : NavigationRoute
}
