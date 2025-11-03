package com.fhzapps.bodytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.fhzapps.bodytrack.BodyPage.BodyPageListView
import com.fhzapps.bodytrack.networking.ApiInterface
import com.fhzapps.bodytrack.networking.RetrofitInstance
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme

class MainActivity : ComponentActivity() {
    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            BodyTrackTheme {

                val navController = rememberNavController()
                NavigationRoot(navController)
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    BodyPageListView(innerPadding)
//                }
            }
        }

    }

    private fun getApiInterface() {
        apiInterface = RetrofitInstance.apiInterface
    }
}
