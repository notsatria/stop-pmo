package dev.notsatria.stop_pmo.ui.screen.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

data class OnboardingPage(
    val title: String,
    val body: String,
    val animationRes: Int,
    val features: List<Pair<Int, String>>? = null
)

@Composable
fun OnboardingRoute(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    OnboardingScreen(
        onComplete = {
            viewModel.completeOnboarding()
            onComplete()
        }
    )
}

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit = {}
) {
    val theme = LocalTheme.current
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = "You've Already Taken the Hardest Step",
            body = "Deciding to change takes courage. This app is here to support you every day of your journey \u2014 no judgment, just progress.",
            animationRes = R.raw.rocket_animation
        ),
        OnboardingPage(
            title = "Track Your Progress",
            body = "Every second counts. Watch your streak grow in real-time. If you slip, log it honestly \u2014 each relapse teaches you something.",
            animationRes = R.raw.checklist_animation,
            features = listOf(
                R.drawable.ic_time to "Live streak counter",
                R.drawable.ic_dashboard to "Relapse logging with notes",
                R.drawable.ic_analytics to "Analytics to spot patterns",
                R.drawable.ic_favorite to "Milestone notifications"
            )
        ),
        OnboardingPage(
            title = "Your Journey Starts Now",
            body = "You are stronger than you think. Take it one day at a time.",
            animationRes = R.raw.trophy_animation
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    BackHandler {}

    Scaffold { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.streakScreenSurface)
        ) {
            Column(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    OnboardingPageContent(page = pages[page])
                }

                PageIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                BottomBar(
                    pagerState = pagerState,
                    onSkip = onComplete,
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onStart = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val theme = LocalTheme.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(page.animationRes))
    val backgroundAnim by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.background_prize_animation))
    val backgroundAnimProgress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        iterations = 99
    )
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        iterations = 99
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
            LottieAnimation(
                modifier = Modifier.size(300.dp),
                composition = backgroundAnim,
                progress = { backgroundAnimProgress }
            )
            LottieAnimation(
                modifier = Modifier.size(200.dp),
                composition = composition,
                progress = { progress }
            )
        }

        Spacer(Modifier.height(40.dp))

        Text(
            page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            page.body,
            style = MaterialTheme.typography.bodyLarge,
            color = theme.streakTextSecondary,
            textAlign = TextAlign.Center
        )

        page.features?.let { features ->
            Spacer(Modifier.height(32.dp))
            features.forEach { (icon, label) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = theme.streakForeground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val theme = LocalTheme.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { index ->
            val isActive = pagerState.currentPage == index
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) theme.streakForeground
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun BottomBar(
    pagerState: PagerState,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalTheme.current
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLastPage) {
            TextButton(onClick = onSkip) {
                Text(
                    "Skip",
                    color = theme.streakForeground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            Spacer(Modifier)
        }

        if (!isLastPage) {
            TextButton(onClick = onNext) {
                Text(
                    "Next",
                    color = theme.streakForeground,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        } else {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.streakButtonBackground,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Start Your Journey",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
