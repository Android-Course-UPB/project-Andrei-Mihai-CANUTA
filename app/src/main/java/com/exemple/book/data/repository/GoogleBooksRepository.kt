package com.exemple.book.data.repository


import com.exemple.book.data.remote.RetrofitInstance

class GoogleBooksRepository {
    suspend fun searchBooks(query: String) =
        RetrofitInstance.api.searchBooks(query)
}