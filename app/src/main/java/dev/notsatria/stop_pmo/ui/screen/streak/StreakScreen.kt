package dev.notsatria.stop_pmo.ui.screen.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun StreakRoute(
    modifier: Modifier = Modifier,
    navigateToDashboard: () -> Unit = {},
    viewModel: StreakViewModel = viewModel()
) {
    StreakScreen(modifier, navigateToDashboard, viewModel.streakCount)
}

@Composable
fun StreakScreen(
    modifier: Modifier = Modifier,
    navigateToDashboard: () -> Unit = {},
    streakCount: Int = 7
) {
    val theme = LocalTheme.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire_animation))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        iterations = 99
    )
    Scaffold(modifier) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.streakScreenSurface)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    modifier = Modifier.size(200.dp),
                    composition = composition,
                    progress = { progress }
                )
                Spacer(Modifier.height(60.dp))
                Text(
                    "$streakCount Days Streak",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "You're on fire! Keep up the great work and stay strong.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = theme.streakTextSecondary
                )
                Spacer(Modifier.height(60.dp))
                Button(
                    onClick = navigateToDashboard,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.streakButtonBackground,
                        contentColor = theme.streakScreenSurface
                    )
                ) {
                    Text(
                        "Back to Dashboard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StreakScreenPreview(modifier: Modifier = Modifier) {
    StreakScreen()
}