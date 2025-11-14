package com.example.movieapp.model

import com.example.movieapplication.ui.details.Genre
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
): Serializable


data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?,
    val order: Int
)

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
    val crew: List<CrewMember>?,
    val cast: List<CastMember>?
) : Serializable

data class CrewMember(
    val id: Int,
    val name: String,
    val job: String?,  // Director, Writer, Producer...
    val department: String?
)
data class MovieVideoResponse(
    val id: Int,
    val results: List<MovieVideo>
)

data class MovieVideo(
    val key: String,    // YouTube key
    val name: String,
    val site: String,   // "YouTube"
    val type: String    // "Trailer", "Teaser", ...
)
data class MovieCreditsResponse(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)