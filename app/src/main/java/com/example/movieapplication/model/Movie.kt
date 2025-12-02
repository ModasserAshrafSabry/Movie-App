package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?,
    val order: Int
)

