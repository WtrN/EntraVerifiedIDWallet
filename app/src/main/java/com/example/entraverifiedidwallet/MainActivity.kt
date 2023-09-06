package com.example.entraverifiedidwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
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
    val context = LocalContext.current

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable("vc") { it ->
            val entraClient: EntraClientViewModel = viewModel(
                viewModelStoreOwner = it.rememberParentEntry(
                    navController = navController
                ),
                factory = EntraClientViewModel.provideViewModel(context),
            )
            RequestVCUrl(
                onNavigate = { navController.navigate("inputPin/${it}") },
                entraClient = entraClient,
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
        ) { it ->
            val entraClient: EntraClientViewModel = viewModel(
                viewModelStoreOwner = it.rememberParentEntry(
                    navController = navController
                ),
                factory = EntraClientViewModel.provideViewModel(context),
            )
            InputRequirement(
                onNavigate = { navController.navigate("vc") },
                url = URLDecoder.decode(
                    it.arguments?.getString("url") ?: "",
                    StandardCharsets.UTF_8.toString()
                ),
                entraClient = entraClient,
            )
        }
    }
}

@Composable
fun NavBackStackEntry.rememberParentEntry(
    navController: NavController,
): NavBackStackEntry {
    val parentId = destination.parent!!.id
    return remember(this) {
        navController.getBackStackEntry(parentId)
    }
}
