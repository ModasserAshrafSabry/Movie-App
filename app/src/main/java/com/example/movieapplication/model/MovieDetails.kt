package com.example.movieapplication.model

import com.example.movieapp.model.CastMember
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieDetails(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    val voteAverage: Double?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val genres: List<Genre>?,
    val runtime: Int?,
    val cast: List<CastMember>?,
    val crew: List<CrewMember>?,
    val videos: VideoResponse? = null

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
data class Video(
    val id: String,
    val key: String,       // YouTube video key
    val name: String?,
    val site: String?,     // e.g., "YouTube"
    val type: String?      // e.g., "Trailer"
)

data class VideoResponse(
    val results: List<Video> = emptyList()
)

data class VideoResult(
    val key: String,
    val site: String,
    val type: String
)

