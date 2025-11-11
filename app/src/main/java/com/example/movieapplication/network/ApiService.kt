package com.example.movieapp.network

import com.example.movieapp.model.MovieResponse
import com.example.movieapp.model.CelebrityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("discover/movie")
    suspend fun getMovieByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int
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

    @GET("search/person")
    suspend fun searchCelebrities(
        @Query("api_key") apiKey: String,
        @Query("query") query: String // 🔤 الكلمة اللي بيبحث بيها المستخدم
    ): CelebrityResponse
}
