package com.example.zuppsmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zuppsmobile.ZuppsApplication
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.zuppsmobile.ui.screens.*
import com.example.zuppsmobile.viewmodel.AuthViewModel
import com.example.zuppsmobile.viewmodel.AuthViewModelFactory
import com.example.zuppsmobile.viewmodel.ProductViewModel
import com.example.zuppsmobile.viewmodel.ProductViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val app = context.applicationContext as ZuppsApplication

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(app.repository)
    )

    // Instanciando o novo ViewModel do Cardápio
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(app.productRepository)
    )

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.clearError()
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                authViewModel = authViewModel,
                productViewModel = productViewModel, // Passando o ViewModel para o Dashboard
                onNavigateToAddProduct = {
                    navController.navigate("add_product") // Navega para o formulário
                },
                onNavigateToEditProduct = { productId ->
                    navController.navigate("edit_product/$productId")
                },
                onNavigateToManageRestaurants = {
                    navController.navigate("restaurant_list")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("add_product") {
            AddProductScreen(
                viewModel = productViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "edit_product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            EditProductScreen(
                productId = productId,
                viewModel = productViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("restaurant_list") {
            RestaurantListScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditRestaurant = { id ->
                    navController.navigate("edit_restaurant/$id")
                }
            )
        }

        composable(
            route = "edit_restaurant/{restaurantId}",
            arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            EditRestaurantScreen(
                restaurantId = restaurantId,
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}