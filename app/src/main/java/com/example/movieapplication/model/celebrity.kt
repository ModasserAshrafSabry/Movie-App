package com.example.movieapp.model

import com.google.gson.annotations.SerializedName

// 🎯 تمثل كائن "مشهور" (Celebrity) راجع من API
data class Celebrity(
    val id: Int,                      // 🆔 رقم تعريف المشهور
    val name: String?,                // 👤 اسم المشهور
    val birthday: String?,
    val place_of_birth: String?,
    val biography: String?,
    @SerializedName("known_for_department") val role: String?, // 🎭 المجال اللي مشهور فيه (تمثيل، إخراج، إلخ)
    @SerializedName("profile_path") val profilePath: String?   // 🖼️ رابط صورة المشهور
)
