package com.exemple.book.data.repository

import com.exemple.book.data.local.BookDao
import com.exemple.book.domain.model.Book
import kotlinx.coroutines.flow.Flow

class BookRepository(private val dao: BookDao) {
    fun getAllBooks(): Flow<List<Book>> = dao.getAllBooks()
    suspend fun insertBook(book: Book) = dao.insertBook(book)
    suspend fun deleteBook(book: Book) = dao.deleteBook(book)
}