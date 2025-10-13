package dev.notsatria.stop_pmo.ui.screen.settings

import android.content.res.Configuration
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.notsatria.stop_pmo.BuildConfig
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.UiMode
import dev.notsatria.stop_pmo.utils.testWorker
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun SettingRoute(modifier: Modifier = Modifier, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    SettingScreen(
        modifier,
        uiState = uiState,
        onToggle = { isEnabled, title ->
            createOnToggleHandler(title, isEnabled, viewModel)
        }
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    uiState: SettingState = SettingState(),
    onToggle: ((isEnabled: Boolean, title: String) -> Unit)? = null
) {
    val theme = LocalTheme.current
    val context = LocalContext.current

    val groupedSettings = remember(uiState) { uiState.settings.groupBy { it.group } }

    val preferenceGroup = groupedSettings["Preferences"].orEmpty()
    val moreGroup = groupedSettings["More"].orEmpty()
    val versionGroup = groupedSettings["-"].orEmpty()

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

            if (preferenceGroup.isNotEmpty()) {
                item(key = "header_preference") {
                    GroupHeader(title = SettingsGroup.PREFERENCES, style = headerStyle)
                }
                items(
                    items = preferenceGroup,
                    key = { it.id }
                ) { setting ->
                    SettingItem(setting = setting, onToggleClick = { isEnabled, title ->
                        onToggle?.invoke(isEnabled, title)
                    })
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (moreGroup.isNotEmpty()) {
                item(key = "more_box") {
                    GroupHeader(title = SettingsGroup.MORE, style = headerStyle)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.settingItemBackground)
                    ) {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            moreGroup.forEachIndexed { index, setting ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
//                                        .clickable(enabled = setting.onClick != null) { setting.onClick?.invoke() }
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

            if (BuildConfig.DEBUG) {
                item {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        testWorker(context, epoch = 1759741200)
                    }) {
                        Text("Test Streak Worker")
                    }
                }
            }

            item { Spacer(Modifier.height(innerPadding.calculateBottomPadding() + 20.dp)) }
        }
    }
}

private fun createOnToggleHandler(
    title: String,
    isEnabled: Boolean,
    viewModel: SettingsViewModel,
) {
    when (title) {
        SettingsTitle.DARK_MODE -> {
            val uiMode = if (isEnabled) UiMode.DARK else UiMode.LIGHT
            viewModel.toggleDarkMode(uiMode)
        }

        SettingsTitle.PUSH_NOTIFICATIONS -> viewModel.toggleNotifications(isEnabled)
        SettingsTitle.TIME_FORMAT -> viewModel.toggleTimeFormat(isEnabled)
        else -> {}
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
    SettingScreen(
        uiState = SettingState(
            settings = createSettingsList(
                uiMode = UiMode.DARK,
                notificationsEnabled = true,
                timeFormat24H = false
            )
        )
    )
}