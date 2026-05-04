package dev.notsatria.stop_pmo.ui.screen.streak

import android.R.attr.theme
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.notsatria.reboot.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.Capturable
import dev.notsatria.stop_pmo.utils.rememberCaptureController
import java.io.File
import java.io.FileOutputStream

@Composable
fun StreakRoute(
    modifier: Modifier = Modifier,
    navigateToDashboard: () -> Unit = {},
    viewModel: StreakViewModel = viewModel()
) {
    StreakScreen(
        modifier,
        navigateToDashboard,
        viewModel.streakCount
    )
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
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // Handle sharing when bitmap is captured
    LaunchedEffect(capturedBitmap) {
        capturedBitmap?.let { bitmap ->
            shareToInstagramStory(context, bitmap.asAndroidBitmap())
            capturedBitmap = null // Reset after sharing
        }
    }

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
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        captureController.capture()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, theme.streakButtonBackground)
                ) {
                    Text(
                        "Share to Instagram Story",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(1080.dp, 1920.dp)
                .offset(x = (-2000).dp)
        ) {
            Capturable(
                controller = captureController,
                onCaptured = { bitmap ->
                    capturedBitmap = bitmap
                }
            ) {
                ShareableContent(streakCount = streakCount)
            }
        }
    }
}

@Preview
@Composable
fun ShareableContent(streakCount: Int = 7) {
    val theme = LocalTheme.current
    Box(
        Modifier
            .background(theme.streakScreenSurface)
            .border(BorderStroke(width = 12.dp, color = Color.White), RoundedCornerShape(32.dp))
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🔥",
                style = TextStyle(fontSize = 200.sp)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                "$streakCount Days Streak",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 64.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "I have made my $streakCount days streak! Let's keep going and stay strong together.",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                textAlign = TextAlign.Center,
                color = theme.streakTextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(R.drawable.ic_logo),
                    "Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(
                            CircleShape
                        )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Stop PMO",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
            }
        }
    }
}

private fun shareToInstagramStory(context: Context, bitmap: Bitmap) {
    val instagramPackage = "com.instagram.android"

    // Check if Instagram is installed
    val pm = context.packageManager
    try {
        pm.getPackageInfo(instagramPackage, PackageManager.GET_ACTIVITIES)
    } catch (e: PackageManager.NameNotFoundException) {
        Toast.makeText(context, "Instagram is not installed", Toast.LENGTH_SHORT).show()
        return
    }

    // Save bitmap to cache
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "streak_story.png")

    try {
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Share to Instagram Stories
        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(contentUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra("content_url", contentUri)

            // Optional: Add app attribution
            putExtra("top_background_color", "#000000")
            putExtra("bottom_background_color", "#000000")
        }

        context.grantUriPermission(
            instagramPackage,
            contentUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        if (intent.resolveActivity(pm) != null) {
            context.startActivity(intent)
        } else {
            // Fallback: Regular share
            shareImageFallback(context, contentUri)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareImageFallback(context: Context, contentUri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setPackage("com.instagram.android")
    }

    try {
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        // If Instagram fails, show general share dialog
        val generalIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(generalIntent, "Share Streak"))
    }
}

@Preview
@Composable
fun StreakScreenPreview(modifier: Modifier = Modifier) {
    StreakScreen()
}