package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

data class Celebrity(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "Unknown",

    @SerializedName("known_for_department")
    val role: String? = null,

    @SerializedName("profile_path")
    val profilePath: String? = null,

    @SerializedName("birthday")
    val birthday: String? = null,

    @SerializedName("place_of_birth")
    val placeOfBirth: String? = null,

    @SerializedName("biography")
    val biography: String? = null,

    var profileImagePaths: List<String> = emptyList()
) {
    // Make sure we have proper equality checks
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Celebrity

        if (id != other.id) return false
        if (name != other.name) return false
        if (role != other.role) return false
        if (profilePath != other.profilePath) return false
        if (birthday != other.birthday) return false
        if (placeOfBirth != other.placeOfBirth) return false
        if (biography != other.biography) return false
        if (profileImagePaths != other.profileImagePaths) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (role?.hashCode() ?: 0)
        result = 31 * result + (profilePath?.hashCode() ?: 0)
        result = 31 * result + (birthday?.hashCode() ?: 0)
        result = 31 * result + (placeOfBirth?.hashCode() ?: 0)
        result = 31 * result + (biography?.hashCode() ?: 0)
        result = 31 * result + profileImagePaths.hashCode()
        return result
    }
}