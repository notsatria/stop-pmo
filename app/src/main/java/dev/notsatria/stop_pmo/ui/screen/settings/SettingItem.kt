package dev.notsatria.stop_pmo.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    setting: Setting,
    onToggleClick: ((isEnabled: Boolean, title: String) -> Unit)
) {
    val theme = LocalTheme.current
    val hasToggle = setting.isEnabled != null

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
//            .clickable(enabled = hasClick) { if (hasClick) setting.onClick.invoke() }
            .background(theme.settingItemBackground)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    setting.title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = theme.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    setting.description ?: "",
                    style = TextStyle(fontSize = 14.sp, color = theme.textSecondary)
                )
            }
            Spacer(Modifier.weight(1f))
            if (hasToggle) {
                Switch(
                    checked = setting.isEnabled,
                    onCheckedChange = { onToggleClick(it, setting.title) },
                    modifier = Modifier,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = theme.buttonPrimary,
                        uncheckedThumbColor = theme.buttonDisabled,
                        checkedTrackColor = theme.buttonPrimary.copy(alpha = 0.5f),
                        uncheckedTrackColor = theme.buttonDisabled.copy(alpha = 0.5f)
                    )
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = LocalTheme.current.iconSecondary
                )
            }
        }
    }
}