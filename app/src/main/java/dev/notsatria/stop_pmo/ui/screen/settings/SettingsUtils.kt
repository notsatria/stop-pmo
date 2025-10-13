package dev.notsatria.stop_pmo.ui.screen.settings

import dev.notsatria.stop_pmo.utils.UiMode
import java.util.UUID

object SettingsTitle {
    const val PUSH_NOTIFICATIONS = "Streak Notifications"
    const val DARK_MODE = "Dark Mode"
    const val TIME_FORMAT = "Time Format"
    const val PRIVACY_POLICY = "Privacy Policy"
    const val TERMS_OF_SERVICE = "Terms of Service"
    const val SEND_FEEDBACK = "Send Feedback"
    const val APP_VERSION = "App Version"
}

object SettingsGroup {
    const val PREFERENCES = "Preferences"
    const val MORE = "More"
}

data class Setting(
    val id: String = UUID.randomUUID().toString(),
    val group: String,
    val title: String,
    val description: String? = null,
    val isEnabled: Boolean? = null,
)

fun createSettingsList(
    uiMode: Int,
    notificationsEnabled: Boolean,
    timeFormat24H: Boolean
): List<Setting> = listOf(
    Setting(
        group = SettingsGroup.PREFERENCES,
        title = SettingsTitle.PUSH_NOTIFICATIONS,
        description = "Enable or disable push Streak Notifications",
        isEnabled = notificationsEnabled,
    ),
    Setting(
        group = SettingsGroup.PREFERENCES,
        title = SettingsTitle.DARK_MODE,
        description = "Enable or disable dark mode",
        isEnabled = uiMode == UiMode.DARK,
    ),
    Setting(
        group = SettingsGroup.PREFERENCES,
        title = SettingsTitle.TIME_FORMAT,
        description = "Use 24 Hours format",
        isEnabled = timeFormat24H,
    ),
    Setting(
        group = SettingsGroup.MORE,
        title = SettingsTitle.PRIVACY_POLICY,
    ),
    Setting(
        group = SettingsGroup.MORE,
        title = SettingsTitle.TERMS_OF_SERVICE,
    ),
    Setting(
        group = SettingsGroup.MORE,
        title = SettingsTitle.SEND_FEEDBACK,
    ),
    Setting(
        group = "-",
        title = SettingsTitle.APP_VERSION
    ),
)