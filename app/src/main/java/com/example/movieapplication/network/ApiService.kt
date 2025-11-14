package com.example.movieapp.network

import com.example.movieapp.model.MovieResponse
import com.example.movieapp.model.CelebrityResponse
import com.example.movieapplication.model.CreditsResponse
import com.example.movieapplication.model.MovieDetails
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("trending/person/week")
    suspend fun getTrendingCelebrities(
        @Query("api_key") apiKey: String
    ): CelebrityResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): MovieResponse

    @GET("search/person")
    suspend fun searchCelebrities(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): CelebrityResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetails

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse

}
