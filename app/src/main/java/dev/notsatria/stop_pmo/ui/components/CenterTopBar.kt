package dev.notsatria.stop_pmo.ui.components

import android.R.attr.theme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTopBar(modifier: Modifier = Modifier, title: String) {
    val theme = LocalTheme.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = theme.surface,
            titleContentColor = theme.textPrimary
        ),
    )
}