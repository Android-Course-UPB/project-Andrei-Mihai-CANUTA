package com.exemple.book.presentation.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.book.domain.model.Book
import com.exemple.book.navigation.Screen
import com.exemple.book.presentation.viewmodel.BookViewModel

enum class BookStatus(val label: String, val backgroundColor: Color, val textColor: Color) {
    SHOULD_READ("Should Read", Color(0xFFD3D3D3), Color.White),
    READING("Reading", Color(0xFFADD8E6), Color.White),
    READ("Read", Color(0xFF90EE90), Color.White)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(context))
    val books = viewModel.books.collectAsState().value

    var selectedFilter by remember { mutableStateOf<BookStatus?>(null) }
    val filteredBooks = books.filter {
        selectedFilter == null || it.status.equals(selectedFilter!!.name, ignoreCase = true)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }
    var bookToUpdate by remember { mutableStateOf<Book?>(null) }

    if (showDeleteDialog && bookToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Book") },
            text = { Text("Are you sure you want to delete \"${bookToDelete!!.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook(bookToDelete!!)
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStatusDialog && bookToUpdate != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Change Status") },
            text = {
                Column {
                    BookStatus.entries.forEach { status ->
                        Text(
                            text = status.label,
                            color = status.textColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(status.backgroundColor)
                                .clickable {
                                    val updated = bookToUpdate!!.copy(status = status.name)
                                    viewModel.insertBook(updated)
                                    showStatusDialog = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Bookshelf") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddEditBook.route)
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BookStatus.entries.forEach { status ->
                    val isSelected = selectedFilter == status

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) status.backgroundColor else Color.Transparent,
                        shadowElevation = if (isSelected) 4.dp else 0.dp,
                        border = if (!isSelected) BorderStroke(1.dp, Color.Gray) else null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable {
                                selectedFilter = if (isSelected) null else status
                            }
                    ) {
                        Text(
                            text = status.label,
                            color = if (isSelected) status.textColor else Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            LazyColumn {
                items(filteredBooks) { book ->
                    val currentStatus = try {
                        BookStatus.valueOf(book.status.uppercase())
                    } catch (e: Exception) {
                        BookStatus.SHOULD_READ
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = {
                                    bookToDelete = book
                                    showDeleteDialog = true
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Delete")
                            }
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = book.author)
                                    Button(
                                        onClick = {
                                            bookToUpdate = book
                                            showStatusDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = currentStatus.backgroundColor,
                                            contentColor = currentStatus.textColor
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = currentStatus.label)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}