package com.exemple.book.presentation.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.exemple.book.domain.model.Book
import com.exemple.book.presentation.viewmodel.BookViewModel
import com.exemple.book.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookScreen() {
    val context = LocalContext.current.applicationContext as Application
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(context))
    val searchViewModel: SearchViewModel = viewModel()

    val coroutineScope = rememberCoroutineScope()

    val searchResults = searchViewModel.searchResults.collectAsState().value
    val isLoading = searchViewModel.isLoading.collectAsState().value
    val error = searchViewModel.error.collectAsState().value

    val snackbarHostState = remember { SnackbarHostState() }
    var bookToPreview by remember { mutableStateOf<Book?>(null) }
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search Books") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (query.length >= 3) {
                        searchViewModel.searchBooks(query)
                    }
                },
                label = { Text("Search books") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            error?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(searchResults) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                bookToPreview = Book(
                                    title = item.volumeInfo.title ?: "No title",
                                    author = item.volumeInfo.authors?.joinToString() ?: "Unknown author",
                                    description = item.volumeInfo.description,
                                    rating = item.volumeInfo.averageRating?.toString(),
                                    thumbnailUrl = item.volumeInfo.imageLinks?.thumbnail,
                                    status = "SHOULD_READ"
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.volumeInfo.title ?: "No title",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.volumeInfo.authors?.joinToString() ?: "Unknown author"
                                )
                            }
                            Button(onClick = {
                                val book = Book(
                                    title = item.volumeInfo.title ?: "Untitled",
                                    author = item.volumeInfo.authors?.joinToString() ?: "Unknown",
                                    description = item.volumeInfo.description,
                                    rating = item.volumeInfo.averageRating?.toString(),
                                    thumbnailUrl = item.volumeInfo.imageLinks?.thumbnail,
                                    status = "SHOULD_READ"
                                )
                                coroutineScope.launch {
                                    bookViewModel.insertBook(book)
                                    snackbarHostState.showSnackbar("Book added to library")
                                }
                            }) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }

        if (bookToPreview != null) {
            AlertDialog(
                onDismissRequest = { bookToPreview = null },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        bookToPreview!!.thumbnailUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Book Cover",
                                modifier = Modifier
                                    .height(180.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(bookToPreview!!.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        Text("Author: ${bookToPreview!!.author}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description: ${bookToPreview!!.description ?: "No description available."}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rating: ${bookToPreview!!.rating ?: "Not rated"}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            bookViewModel.insertBook(bookToPreview!!.copy(status = "SHOULD_READ"))
                            bookToPreview = null
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookToPreview = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}