package com.example.participate // Update with your actual package name
import androidx.compose.ui.viewinterop.AndroidView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            EcoContributorTheme {
                EcoContributorApp()
            }
        }
    }
}

@Composable
fun EcoContributorTheme(content: @Composable () -> Unit) {
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
fun EcoContributorApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("Ready to explore citizen science!") }
    var webViewContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
            ) {
                Text(
                    text = "EcoContributor",
                    fontSize = 28.sp,
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Cursive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        bottomBar = {
            Text(
                text = statusText,
                color = Color(0xFF00FF00),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                        .weight(1f)
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
                        .weight(1f)
                        .padding(4.dp)
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
                        .weight(1f)
                        .padding(4.dp),
                    enabled = !isLoading
                ) {
                    Text("Login")
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
                    webViewContent.startsWith("projects:") -> {
                        ProjectsList(webViewContent.substringAfter("projects:"))
                    }
                    webViewContent.isNotEmpty() -> {
                        SciStarterWebView(webViewContent)
                    }
                    else -> {
                        Text(
                            text = "Select a tab to begin",
                            color = Color(0xFF00FF00),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SciStarterWebView(contentType: String) {
    val url = when (contentType) {
        "finder" -> "https://scistarter.org/finder"
        "login" -> "https://scistarter.org/api/oauth/authorize?key=YOUR_API_KEY_HERE&redirect_uri=https://yourapp.com/callback"
        "myprojects" -> "https://scistarter.org/dashboard" // This shows your participated projects
        else -> "https://scistarter.org"
    }

    AndroidView(
        factory = { context ->
            android.webkit.WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = android.webkit.WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)

                // IMPORTANT: Enable cookies so login session persists
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
fun ProjectsList(projectsJson: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        if (projectsJson.isEmpty() || projectsJson == "error") {
            Text(
                text = "Unable to load projects. Check your API key and internet connection.",
                color = Color(0xFF00FF00),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // Parse projects outside of composable context
            val parsedProjects = remember(projectsJson) {
                try {
                    val projects = JSONArray(projectsJson)
                    val projectList = mutableListOf<Pair<String, String>>()
                    for (i in 0 until minOf(projects.length(), 10)) {
                        val project = projects.getJSONObject(i)
                        val name = project.optString("name", "Unnamed Project")
                        val description = project.optString("description", "No description available")
                        projectList.add(Pair(name, description))
                    }
                    projectList
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

            if (parsedProjects.isEmpty()) {
                Text(
                    text = "Error parsing projects data",
                    color = Color(0xFF00FF00),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Display the parsed projects
                parsedProjects.forEach { (name, description) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A),
                            contentColor = Color(0xFF00FF00)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = name,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Cursive
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
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
                .url("https://scistarter.org/api/projects?key=YOUR_API_KEY_HERE")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: "error"
            } else {
                "error"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "error"
        }
    }
}