package com.example.movieapp.network

import com.example.movieapp.model.MovieResponse
import com.example.movieapp.model.CelebrityResponse
import retrofit2.http.GET
import retrofit2.http.Query

// 🎯 واجهة Retrofit لتحديد طلبات API
interface ApiService {

    // 🎬 جلب الأفلام التريندينج
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String // 🔑 مفتاح API من TMDB
    ): MovieResponse

    // 🌟 جلب المشاهير التريندينج
    @GET("trending/person/week")
    suspend fun getTrendingCelebrities(
        @Query("api_key") apiKey: String
    ): CelebrityResponse

    // 🔍 البحث عن فيلم
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String // 🔤 الكلمة اللي بيبحث بيها المستخدم
    ): MovieResponse
}
