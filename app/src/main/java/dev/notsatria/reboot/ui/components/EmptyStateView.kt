package dev.notsatria.stop_pmo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun EmptyStateView(
    modifier: Modifier = Modifier,
    onRelapseClick: () -> Unit = {},
    isButtonRelapseVisible: Boolean = false
) {
    val theme = LocalTheme.current
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(color = theme.buttonPrimary.copy(alpha = 0.2f))
        ) {
            Icon(
                painterResource(R.drawable.ic_time),
                null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                tint = theme.buttonPrimary
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "No Relapses Logged Yet",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Your journey to recovery starts here. Keep track of your progress and stay motivated!",
            style = MaterialTheme.typography.bodyMedium,
            color = theme.textSecondary,
            modifier = Modifier
                .padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        if (isButtonRelapseVisible) Button(
            modifier = Modifier,
            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonPrimary),
            onClick = onRelapseClick
        ) {
            Text(
                "Log Relapse",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        EmptyStateView()
    }
}