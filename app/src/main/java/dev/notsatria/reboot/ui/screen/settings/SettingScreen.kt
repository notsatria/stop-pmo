package dev.notsatria.stop_pmo.ui.screen.settings

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.content.ContextCompat
import com.notsatria.reboot.R
import com.orhanobut.logger.BuildConfig
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DebugWorkScheduler
import dev.notsatria.stop_pmo.utils.UiMode
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
    navigateToStreakScreen: () -> Unit = {},
    navigateToWebView: (url: String, title: String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.toggleNotifications(true)
            } else {
                viewModel.toggleNotifications(false)
            }
        }

    SettingScreen(
        modifier,
        context = context,
        uiState = uiState,
        onToggle = { isEnabled, title ->
            when (title) {
                SettingsTitle.PUSH_NOTIFICATIONS -> {
                    handleNotificationPermission(
                        context = context,
                        isEnabled = isEnabled,
                        launcher = launcher,
                        viewModel = viewModel
                    )
                }

                else -> {
                    createOnToggleHandler(title, isEnabled, viewModel)
                }
            }
        },
        onItemClick = { title ->
            when (title) {
                SettingsTitle.SEND_FEEDBACK -> { /* handled inside SettingScreen */ }
                SettingsTitle.PRIVACY_POLICY -> {
                    navigateToWebView("https://reboot.notsatria.com/privacy-policy", "Privacy Policy")
                }
                SettingsTitle.TERMS_OF_SERVICE -> {
                    navigateToWebView("https://reboot.notsatria.com/terms-of-service", "Terms of Service")
                }
            }
        },
        navigateToStreakScreen = navigateToStreakScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    uiState: SettingState = SettingState(),
    onToggle: ((isEnabled: Boolean, title: String) -> Unit)? = null,
    onItemClick: (String) -> Unit = {},
    navigateToStreakScreen: () -> Unit = {}
) {
    val theme = LocalTheme.current
    var showFeedbackDialog by remember { mutableStateOf(false) }

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
                                        .clickable {
                                            if (setting.title == SettingsTitle.SEND_FEEDBACK) {
                                                showFeedbackDialog = true
                                            } else {
                                                onItemClick(setting.title)
                                            }
                                        }
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
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        DebugWorkScheduler.scheduleImmediateStreakCheck(context, 7)
                    }) {
                        Text("Test Streak Worker")
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        navigateToStreakScreen()
                    }) {
                        Text("Test Streak Screen")
                    }
                }
            }

            item { Spacer(Modifier.height(innerPadding.calculateBottomPadding() + 20.dp)) }
        }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            context = context,
            onDismiss = { showFeedbackDialog = false }
        )
    }
}

private fun handleNotificationPermission(
    context: Context,
    isEnabled: Boolean,
    launcher: ActivityResultLauncher<String>,
    viewModel: SettingsViewModel
) {
    if (!isEnabled) {
        // User wants to disable notifications - no permission check needed
        viewModel.toggleNotifications(false)
        return
    }

    // User wants to enable notifications
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.toggleNotifications(true)
        } else {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        // Pre-Android 13, notifications don't require runtime permission
        viewModel.toggleNotifications(true)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackDialog(
    context: Context,
    onDismiss: () -> Unit
) {
    val theme = LocalTheme.current
    var feedbackText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = theme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Send Feedback",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "We'd love to hear your thoughts or report an issue.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = theme.textSecondary
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text("Type your feedback here...", color = theme.textSecondary)
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = theme.textPrimary
                )
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (feedbackText.isNotBlank()) {
                        launchFeedbackEmail(context, feedbackText.trim())
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = feedbackText.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.buttonPrimary,
                    disabledContainerColor = theme.buttonDisabled
                )
            ) {
                Text("Submit", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = theme.textSecondary)
            }
        }
    }
}

private fun launchFeedbackEmail(context: Context, feedback: String) {
    val deviceInfo = buildDeviceInfoString(context)
    val body = """
        |$deviceInfo
        |
        |Feedback:
        |$feedback
    """.trimMargin()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("notsatria.dev@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "[StopPMO] Feedback")
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        // No email app available
    }
}

private fun buildDeviceInfoString(context: Context): String {
    val device = "${Build.MANUFACTURER} ${Build.MODEL}"

    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)
    val totalRam = formatBytes(memInfo.totalMem)
    val availRam = formatBytes(memInfo.availMem)

    val stat = StatFs(Environment.getDataDirectory().path)
    val totalStorage = formatBytes(stat.totalBytes)
    val availStorage = formatBytes(stat.availableBytes)

    val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    val appVersion = try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "unknown"
    } catch (_: PackageManager.NameNotFoundException) {
        "unknown"
    }

    return """
        |Device: $device
        |RAM: $totalRam (Free: $availRam)
        |Storage: $totalStorage (Available: $availStorage)
        |Android Version: $androidVersion
        |App Version: $appVersion
    """.trimMargin()
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return if (gb >= 1.0) {
        "%.1f GB".format(gb)
    } else {
        val mb = bytes / (1024.0 * 1024.0)
        "%.0f MB".format(mb)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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