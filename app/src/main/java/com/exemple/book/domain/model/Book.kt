package com.exemple.book.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val status: String,
    val description: String? = null,
    val rating: String? = null,
    val thumbnailUrl: String? = null
)
