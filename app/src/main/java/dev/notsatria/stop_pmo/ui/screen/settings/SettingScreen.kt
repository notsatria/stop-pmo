package dev.notsatria.stop_pmo.ui.screen.settings

import android.content.res.Configuration
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

data class Setting(
    val id: String = java.util.UUID.randomUUID().toString(),
    val group: String,
    val title: String,
    val description: String? = null,
    val isEnabled: Boolean? = null,
    val onToggle: ((Boolean) -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
)

private val settings = listOf(
    Setting(
        group = "Notifications",
        title = "Push Notifications",
        description = "Enable or disable push notifications",
        isEnabled = true,
        onToggle = {}
    ),
    Setting(
        group = "Theme",
        title = "Dark Mode",
        description = "Enable or disable dark mode",
        isEnabled = true,
        onToggle = {}
    ),
    Setting(
        group = "More",
        title = "Privacy Policy",
        onClick = {}
    ),
    Setting(
        group = "More",
        title = "Terms of Service",
        onClick = {}
    ),
    Setting(
        group = "More",
        title = "Send Feedback",
        onClick = {}
    ),
    Setting(
        group = "-",
        title = "App Version",
        onClick = {}
    ),
)

@Composable
fun SettingRoute(modifier: Modifier = Modifier) {
    SettingScreen(modifier)
}

@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val theme = LocalTheme.current

    val groupedSettings = remember { settings.groupBy { it.group } }

    val notifications = groupedSettings["Notifications"].orEmpty()
    val themeGroup = groupedSettings["Theme"].orEmpty()
    val moreGroup = groupedSettings["More"].orEmpty()
    val versionGroup = groupedSettings["-"].orEmpty()

    // Reusable text styles (buat sekali)
    val headerStyle = TextStyle(
        fontSize = 14.sp,
        color = theme.buttonPrimary,
        fontWeight = FontWeight.SemiBold
    )
    val titleStyle = TextStyle(
        fontSize = 16.sp,
        color = theme.textPrimary,
        fontWeight = FontWeight.SemiBold
    )
    val subtitleStyle = TextStyle(
        fontSize = 14.sp,
        color = theme.textSecondary
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterTopBar(title = "Settings")
        },
        containerColor = theme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            state = rememberLazyListState()
        ) {
            item { Spacer(Modifier.height(innerPadding.calculateTopPadding() + 20.dp)) }

            // Notifications group (single toggle item)
            if (notifications.isNotEmpty()) {
                item(key = "header_notifications") {
                    GroupHeader(title = "Notifications", style = headerStyle)
                }
                items(
                    items = notifications,
                    key = { it.id }
                ) { setting ->
                    SettingItem(setting = setting)
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Theme group (single toggle item)
            if (themeGroup.isNotEmpty()) {
                item(key = "header_theme") {
                    GroupHeader(title = "Theme", style = headerStyle)
                }
                items(
                    items = themeGroup,
                    key = { it.id }
                ) { setting ->
                    SettingItem(setting = setting)
                }
                item { Spacer(Modifier.height(32.dp)) }
            }

            // More group (boxed list)
            if (moreGroup.isNotEmpty()) {
                item(key = "more_box") {
                    GroupHeader(title = "More", style = headerStyle)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.settingItemBackground)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            moreGroup.forEachIndexed { index, setting ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = setting.onClick != null) { setting.onClick?.invoke() }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = setting.title, style = titleStyle)
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_right),
                                        contentDescription = null,
                                        tint = theme.iconSecondary
                                    )
                                }

                                if (index != moreGroup.lastIndex) {
                                    HorizontalDivider(
                                        color = theme.divider,
                                        modifier = Modifier.padding(vertical = 0.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Version / footer group
            if (versionGroup.isNotEmpty()) {
                item(key = "version") {
                    Spacer(Modifier.height(32.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.settingItemBackground)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val title = versionGroup.firstOrNull()?.title ?: "Version"
                            Text(title, style = titleStyle)
                            Spacer(Modifier.weight(1f))
                            Text(
                                "1.0.0",
                                style = subtitleStyle
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(innerPadding.calculateBottomPadding() + 20.dp)) }
        }
    }
}

@Composable
private fun GroupHeader(title: String, style: TextStyle) {
    Column {
        Text(title, style = style)
        Spacer(Modifier.height(12.dp))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingScreenPreview() {
    SettingScreen()
}