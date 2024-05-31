package dev.sandrocaseiro.sacocheiotv.models.api

data class AEpisode(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val slug: String,
    val showSlug: String,
    val imageUrl: String,
)
