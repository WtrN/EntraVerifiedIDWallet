package com.example.entraverifiedidwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.entraverifiedidwallet.ui.theme.EntraVerifiedIDWalletTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EntraVerifiedIDWalletTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    WalletScreen()
                }
            }
        }
    }
}

@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "vc",
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable("vc") {
            RequestVCUrl(
                onNavigate = { navController.navigate("inputPin/${it}") }
            )
        }
        composable(
            "inputPin/{url}", arguments = listOf(
                navArgument("url") {
                    // 渡したい値の設定
                    type = NavType.StringType
                    nullable = false
                    defaultValue = ""
                })
        ) { entry ->
            InputRequirement(
                onNavigate = { navController.navigate("vc") },
                url = URLDecoder.decode(
                    entry.arguments?.getString("url") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var state by remember {
        mutableStateOf(9)
    }
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EntraVerifiedIDWalletTheme {
        Greeting("Android")
    }
}
