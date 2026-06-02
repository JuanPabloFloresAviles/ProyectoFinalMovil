package com.example.proyectofinalmovil.services.api

import com.example.proyectofinalmovil.BuildConfig

data class CineUabcsApiConfig(
    val baseUrl: String = BuildConfig.CINE_UABCS_API_BASE_URL,
) {
    private val normalizedBaseUrl = baseUrl.trim().trimEnd('/')

    fun endpoint(path: String): String {
        val normalizedPath = path.trim().trimStart('/')
        val apiPath = if (normalizedPath.startsWith("api/")) normalizedPath else "api/$normalizedPath"
        return "$normalizedBaseUrl/$apiPath"
    }
}
