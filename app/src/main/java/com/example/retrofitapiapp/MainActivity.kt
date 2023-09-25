package com.example.retrofitapiapp

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.retrofitapiapp.ui.theme.RetrofitAPIAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import coil.compose.AsyncImage

// Define your data class representing the expected response structure.
data class Post(
    val copyright: String,
    val explanation: String,
    val title: String,
    val url: String
)

// Define your Retrofit API service interface.
interface ApiService {
    @GET("apod")
    suspend fun getPosts(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String
    ): Response<List<Post>>
}

@Composable
fun MyApp() {
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch posts from the API when the app starts
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/planetary/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val apiKey = "DEMO_KEY"
            val startDate = "2023-09-08"

            val response = apiService.getPosts(apiKey, startDate)

            if (response.isSuccessful) {
                posts = response.body() ?: emptyList()
            } else {
                // Handle API error here
                Log.d("MyApp", "Failed to retrieve!")
            }
        }
    }

    // Display the list of posts
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(posts) { post ->
            PostCard(post)
        }
    }
}
@Composable
fun PostCard(post: Post) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = post.title, fontWeight = FontWeight.Bold)
            if (post.copyright != null) {
                Text(text = post.copyright)
            }
            AsyncImage(
                model = post.url,
                contentDescription = post.title
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyApp() {
    RetrofitAPIAppTheme {
        MyApp()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetrofitAPIAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}