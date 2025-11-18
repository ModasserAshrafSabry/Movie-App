package com.example.movieapplication.model

import com.example.movieapp.model.CastMember
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieDetails(
    val id: Int,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val backdropPath: String?,
    val releaseDate: String?,
    val genres: List<Genre>?,
    val runtime: Int?,
    val cast: List<CastMember>?,
    val crew: List<CrewMember>?

) : Serializable

data class CrewMember(
    val id: Int,
    val name: String,
    val job: String?,
    val department: String?
)

data class CreditsResponse(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?,
    val order: Int
)

data class Genre(
    val id: Int,
    val name: String
)