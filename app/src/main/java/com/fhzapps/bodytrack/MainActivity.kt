package com.fhzapps.bodytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.fhzapps.bodytrack.networking.ExerciseApi
import com.fhzapps.bodytrack.ui.theme.BodyTrackTheme

class MainActivity : ComponentActivity() {
    private lateinit var exerciseApi: ExerciseApi

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
}

