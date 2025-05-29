package com.exemple.book.navigation

import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemple.book.presentation.screens.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController

import androidx.navigation.compose.currentBackStackEntryAsState


sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object BookList : Screen("book_list", "Home", Icons.Default.Book)
    object AddEditBook : Screen("add_edit_book", "Add", Icons.Default.Book)
    object BookDetail : Screen("book_detail/{bookId}", "Details", Icons.Default.Book) {
        fun createRoute(bookId: Int) = "book_detail/$bookId"
    }
    object SearchBook : Screen("search_book", "Search", Icons.Default.Search)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val bottomNavItems = listOf(Screen.BookList, Screen.SearchBook)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            if (currentDestination?.route !in listOf(Screen.BookList.route, Screen.SearchBook.route)) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.BookList.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.BookList.route) {
                BookListScreen(navController)
            }
            composable(Screen.AddEditBook.route) {
                AddEditBookScreen(navController)
            }
            composable(Screen.BookDetail.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: -1
                BookDetailScreen(navController, bookId)
            }
            composable(Screen.SearchBook.route) {
                SearchBookScreen()
            }
        }
    }
}