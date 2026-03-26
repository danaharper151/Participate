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
import androidx.compose.foundation.layout.size
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
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
    var webViewBackAction: () -> Unit by remember { mutableStateOf({}) }
    var selectedTab by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("Explore citizen science through SciStarter") }
    var webViewContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Observe confetti and achievements from ViewModel
    val showConfetti by viewModel.showConfetti.collectAsState()
    val newAchievement by viewModel.newAchievement.collectAsState()
    val unlockedAchievements by viewModel.unlockedAchievements.collectAsState()

    val streakData by viewModel.streakData.collectAsState()
    val dailyActivities by viewModel.dailyActivities.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button - only show when on Discover or My Projects tabs with web content
                if ((selectedTab == 0 || selectedTab == 1) && webViewContent.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            // Trigger webview back navigation
                            webViewBackAction()
                        }
                    ) {
                        Text(
                            text = "←",
                            fontSize = 28.sp,
                            color = Color(0xFF00FF00),
                            fontFamily = FontFamily.Serif
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Title (centered if no back button, left-aligned if back button present)
                Text(
                    text = "Participate",
                    fontSize = 28.sp,
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.weight(1f),
                    textAlign = if ((selectedTab == 0 || selectedTab == 1) && webViewContent.isNotEmpty()) {
                        androidx.compose.ui.text.style.TextAlign.Start
                    } else {
                        androidx.compose.ui.text.style.TextAlign.Center
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
            ) {
                // Show award count and streak
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.earthsmile),
                        contentDescription = "Awards",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = " ${unlockedAchievements.size} Awards | ",
                        color = Color(0xFF00FF00),
                        fontSize = 14.sp
                    )
                    Image(
                        painter = painterResource(id = R.drawable.earthheart),
                        contentDescription = "Day Streak",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = " ${streakData?.currentStreak ?: 0} Day Streak",
                        color = Color(0xFF00FF00),
                        fontSize = 14.sp
                    )
                }
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
                                webViewContent = "finder"
                                statusText = "Browse projects daily to unlock awards"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(0.9f)
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("Discover")
                        }

                        Button(
                            onClick = {
                                selectedTab = 1
                                webViewContent = "myprojects"
                                statusText = "SciStarter Dashboard"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("My Projects")
                        }

                        Button(
                            onClick = {
                                selectedTab = 2
                                webViewContent = "login"
                                statusText = "Tap the menu to sign in to SciStarter"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(0.9f)
                                .padding(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("Login")
                        }
                    }

                    // Second row - Home and Trophy buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Home button (left side, under Discover)
                        Button(
                            onClick = {
                                selectedTab = -1
                                webViewContent = ""
                                statusText = "Explore citizen science through SciStarter"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically  // horizontal alignment
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.grassheart),
                                    contentDescription = "Home",
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Home",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }

                        // Empty space in middle
                        Spacer(modifier = Modifier.weight(1f))

                        // Trophy button (right side, under Login)
                        Button(
                            onClick = {
                                selectedTab = 3
                                webViewContent = "achievements"
                                statusText = "View awards"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.idealightbulb),
                                contentDescription = "Achievements",
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Awards",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Serif
                            )

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
                                streakData = streakData,
                                dailyActivities = dailyActivities
                            )
                        }

                        webViewContent.isNotEmpty() -> {
                            SciStarterWebView(
                                contentType = webViewContent,
                                onBackPressed = { action ->
                                    webViewBackAction = action
                                }
                            )
                        }

                        else -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Welcome",
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
            }


            // Floating Action Button for confetti celebration

                FloatingActionButton(
                    onClick = {
                        viewModel.triggerConfetti()
                        statusText = "Nice work!!"
                    },
                    containerColor = Color(0xFF00FF00),
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ladybug),
                        contentDescription = "Celebrate",
                        modifier = Modifier.size(60.dp)
                    )
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
    onBackPressed: (() -> Unit) -> Unit = {}
) {
    var webView: android.webkit.WebView? by remember { mutableStateOf(null) }

    val url = when (contentType) {
        "finder" -> "https://scistarter.org/finder"
        "login" -> "https://scistarter.org"
        "myprojects" -> "https://scistarter.org/dashboard"
        else -> "https://scistarter.org"
    }

    LaunchedEffect(webView) {
        onBackPressed {
            webView?.let {
                if (it.canGoBack()) {
                    it.goBack()
                }
            }
        }
    }

    // Force reload when contentType changes
    LaunchedEffect(contentType) {
        webView?.loadUrl(url)
    }

    AndroidView(
        factory = { context ->
            android.webkit.WebView(context).apply {
                webView = this
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                // Handle mixed content
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                webViewClient = object : android.webkit.WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url.toString()

                        // Log for debugging
                        android.util.Log.d("WebView", "Loading URL: $requestUrl")

                        // Load all URLs in the WebView
                        view?.loadUrl(requestUrl)
                        return true
                    }

                    override fun onPageStarted(
                        view: android.webkit.WebView?,
                        url: String?,
                        favicon: android.graphics.Bitmap?
                    ) {
                        super.onPageStarted(view, url, favicon)
                        android.util.Log.d("WebView", "Page started: $url")
                    }

                    override fun onReceivedError(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?,
                        error: android.webkit.WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        android.util.Log.e("WebView", "Error loading: ${error?.description}, URL: ${request?.url}")
                    }

                    override fun onReceivedHttpError(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?,
                        errorResponse: android.webkit.WebResourceResponse?
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        android.util.Log.e("WebView", "HTTP Error: ${errorResponse?.statusCode}, URL: ${request?.url}")
                    }
                }
                webChromeClient = object : android.webkit.WebChromeClient() {
                    override fun onCreateWindow(
                        view: android.webkit.WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: android.os.Message?
                    ): Boolean {
                        // Load popup URLs in the same WebView
                        val newWebView = android.webkit.WebView(context)
                        newWebView.webViewClient = object : android.webkit.WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: android.webkit.WebView?,
                                request: android.webkit.WebResourceRequest?
                            ): Boolean {
                                // Load the URL in the main WebView instead
                                this@apply.loadUrl(request?.url.toString())
                                return true
                            }
                        }

                        val transport = resultMsg?.obj as? android.webkit.WebView.WebViewTransport
                        transport?.webView = newWebView
                        resultMsg?.sendToTarget()
                        return true
                    }
                }

                setBackgroundColor(android.graphics.Color.BLACK)

                android.webkit.CookieManager.getInstance().setAcceptCookie(true)
                android.webkit.CookieManager.getInstance()
                    .setAcceptThirdPartyCookies(this, true)

                loadUrl(url)
            }
        },
        update = { view ->
            // Only update if URL changed
            if (view.url != url) {
                view.loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AchievementsScreen(
    unlockedAchievements: List<Achievement>,
    streakData: StreakData?,
    dailyActivities: List<DailyActivity>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)  // Extra bottom padding
    ) {
        Text(
            text = "Your Journey",
            fontSize = 28.sp,
            color = Color(0xFF00FF00),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Streak Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color(0xFF00FF00)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Current Streak: ${streakData?.currentStreak ?: 0} days",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Longest Streak: ${streakData?.longestStreak ?: 0} days",
                    fontSize = 16.sp
                )
                Text(
                    text = "Total Days: ${streakData?.totalDaysOpened ?: 0} days",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar View
        Text(
            text = "Activity Calendar",
            fontSize = 20.sp,
            color = Color(0xFF00FF00),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CalendarView(
            activeDates = dailyActivities.map { it.date },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Awards Section
        Text(
            text = "Unlocked Awards",
            fontSize = 20.sp,
            color = Color(0xFF00FF00),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (unlockedAchievements.isEmpty()) {
            Text(
                text = "Browse projects daily to unlock awards",
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
                        Image(
                            painter = painterResource(
                                id = getDrawableIdFromName(achievement.icon)
                            ),
                            contentDescription = achievement.name,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp)
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
            text = "Keep up the good work!!",
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
