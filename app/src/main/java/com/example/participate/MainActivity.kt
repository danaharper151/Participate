package com.example.participate // Update with your actual package name
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParticipateTheme {
                val viewModel: MainViewModel = viewModel()
                ParticipateApp(viewModel)
            }
        }
    }
}

@Composable
fun ParticipateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF00FF00),
            background = Color.Black,
            surface = Color(0xFF1A1A1A),
            onPrimary = Color.Black,
            onBackground = Color(0xFF00FF00),
            onSurface = Color(0xFF00FF00)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipateApp(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("Ready to explore citizen science!") }
    var webViewContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Observe confetti and achievements from ViewModel
    val showConfetti by viewModel.showConfetti.collectAsState()
    val newAchievement by viewModel.newAchievement.collectAsState()
    val unlockedAchievements by viewModel.unlockedAchievements.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
            ) {
                Text(
                    text = "Participate",
                    fontSize = 28.sp,
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
            ) {
                // Show achievement count
                Text(
                    text = "🏆 ${unlockedAchievements.size} Achievements | 📊 ${allProjects.size} Projects",
                    color = Color(0xFF00FF00),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = statusText,
                    color = Color(0xFF00FF00),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.participate_background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // Content overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                // Tab Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // First row - Main navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                selectedTab = 0
                                statusText = "Loading Project Finder..."
                                webViewContent = "finder"
                                statusText = "Browse projects and click to participate!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(0.9f)  // Smaller weight
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("Discover")
                        }

                        Button(
                            onClick = {
                                selectedTab = 1
                                statusText = "Loading your projects from SciStarter..."
                                webViewContent = "myprojects"
                                statusText = "Viewing your SciStarter dashboard"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(1.2f)  // Larger weight for more space
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("My Projects")
                        }

                        Button(
                            onClick = {
                                selectedTab = 2
                                statusText = "Logging in to SciStarter..."
                                webViewContent = "login"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(0.9f)  // Smaller weight
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("Login")
                        }
                    }
                    // Second row - Trophy button centered below Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Spacer(modifier = Modifier.weight(2f))

                        Button(
                            onClick = {
                                selectedTab = 3
                                webViewContent = "achievements"
                                statusText = "View your achievements!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text("🏆")
                        }
                    }
                }

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0xFF00FF00)
                            )
                        }
                        webViewContent == "achievements" -> {
                            AchievementsScreen(
                                unlockedAchievements = unlockedAchievements,
                                totalProjects = allProjects.size
                            )
                        }
                        webViewContent.isNotEmpty() -> {
                            SciStarterWebView(
                                contentType = webViewContent,
                                onProjectParticipation = { projectName, projectUrl ->
                                    // Record participation when user interacts with a project
                                    viewModel.recordParticipation(projectName, projectUrl)
                                    statusText = "Great work! Participation recorded! 🌱"
                                }
                            )
                        }
                        else -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Welcome to Participate!",
                                    color = Color(0xFF00FF00),
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Serif
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Select a tab to begin",
                                    color = Color(0xFF00FF00),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            } // <-- This closes the Column with background overlay

            // Floating Action Button to record participation
            if (selectedTab == 0 || selectedTab == 1) { // Show on Discover and My Projects tabs
                FloatingActionButton(
                    onClick = {
                        // Show dialog to record participation
                        viewModel.recordParticipation(
                            projectName = "SciStarter Project",
                            projectUrl = "https://scistarter.org"
                        )
                        statusText = "Participation recorded! 🌱"
                    },
                    containerColor = Color(0xFF00FF00),
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("✓", fontSize = 32.sp)
                }
            }

            // Confetti overlay
            if (showConfetti) {
                ConfettiAnimation(
                    modifier = Modifier.fillMaxSize(),
                    onAnimationEnd = { viewModel.hideConfetti() }
                )
            }

            // Achievement unlock dialog
            newAchievement?.let { achievement ->
                AchievementUnlockedDialog(
                    achievement = achievement,
                    onDismiss = { viewModel.dismissAchievement() }
                )
            }
        }
    }
}

@Composable
fun SciStarterWebView(
    contentType: String,
    onProjectParticipation: (String, String) -> Unit = { _, _ -> }
) {
    val url = when (contentType) {
        "finder" -> "https://scistarter.org/finder"
        "login" -> "https://scistarter.org/api/oauth/authorize?key=5XdLfZoqS0vlaczCGl5qXkJuvriLO9D4UXNbPLMB10PAEFt-KgwkRqniTTrgeUO-5PAwyHUhWfDbDAMQ3nYb-Q&redirect_uri=https://yourapp.com/callback"
        "myprojects" -> "https://scistarter.org/dashboard"
        else -> "https://scistarter.org"
    }

    AndroidView(
        factory = { context ->
            android.webkit.WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                webViewClient = object : android.webkit.WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?
                    ): Boolean {
                        // When user clicks on a project, record participation
                        val clickedUrl = request?.url.toString()
                        if (clickedUrl.contains("/project/") || clickedUrl.contains("scistarter.org/")) {
                            // Extract project name from URL if possible
                            val projectName = clickedUrl.substringAfterLast("/").take(50)
                            onProjectParticipation(projectName, clickedUrl)
                        }
                        return false
                    }
                }

                setBackgroundColor(android.graphics.Color.BLACK)

                android.webkit.CookieManager.getInstance().setAcceptCookie(true)
                android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AchievementsScreen(
    unlockedAchievements: List<Achievement>,
    totalProjects: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Text(
            text = "🏆 Your Achievements",
            fontSize = 28.sp,
            color = Color(0xFF00FF00),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Total Projects: $totalProjects",
            fontSize = 20.sp,
            color = Color(0xFF00FF00),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (unlockedAchievements.isEmpty()) {
            Text(
                text = "Start participating in projects to unlock achievements! 🌱",
                fontSize = 18.sp,
                color = Color(0xFF00FF00),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            unlockedAchievements.forEach { achievement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A),
                        contentColor = Color(0xFF00FF00)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = achievement.icon,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = achievement.name,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = achievement.description,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Keep going! 🌍",
            fontSize = 24.sp,
            color = Color(0xFF00FF00),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

suspend fun fetchProjects(): String {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://scistarter.org/api/projects?key=5XdLfZoqS0vlaczCGl5qXkJuvriLO9D4UXNbPLMB10PAEFt-KgwkRqniTTrgeUO-5PAwyHUhWfDbDAMQ3nYb-Q&limit=10")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            android.util.Log.d("SciStarter", "Response code: ${response.code}")
            android.util.Log.d("SciStarter", "Response body: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                responseBody
            } else {
                "error"
            }
        } catch (e: Exception) {
            android.util.Log.e("SciStarter", "Error fetching projects", e)
            e.printStackTrace()
            "error"
        }
    }
}