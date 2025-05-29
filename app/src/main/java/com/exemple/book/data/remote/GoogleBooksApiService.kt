package com.exemple.book.data.remote


import retrofit2.http.GET
import retrofit2.http.Query

data class VolumeResponse(
    val items: List<VolumeItem>?
)

data class VolumeItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val averageRating: Float?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): VolumeResponse
}
