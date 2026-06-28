package com.example.tugas_pab

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    fun getArticles(
        @Query("q") query: String,
        @Query("language") language: String = "id",
        @Query("apiKey") apiKey: String,
        @Query("sortBy") sortBy: String = "publishedAt"
    ): Call<NewsResponse>
}
