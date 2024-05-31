package dev.sandrocaseiro.sacocheiotv.mappings

import dev.sandrocaseiro.sacocheiotv.extensions.toDateFormat
import dev.sandrocaseiro.sacocheiotv.models.api.AEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode

fun AEpisode.toVEpisode(showId: String, showImageUrl: String): VEpisode = VEpisode(
    id = this.id,
    name = this.title,
    slug = this.slug,
    description = this.description,
    imageUrl = this.imageUrl,
    date = this.date.toDateFormat("dd/MM/yyyy"),
    showId = showId,
    showImageUrl = showImageUrl,
)

fun AEpisode.toEEpisode(showId: String): EEpisode = EEpisode(
    id = this.id,
    name = this.title,
    slug = this.slug,
    description = this.description,
    imageUrl = this.imageUrl,
    showId = showId,
    date = this.date.toDateFormat("dd/MM/yyyy")
)

fun EEpisode.toVEpisode(showId: String, showImageUrl: String): VEpisode = VEpisode(
    id = this.id,
    name = this.name,
    slug = this.slug,
    description = this.description,
    imageUrl = this.imageUrl,
    isWatched = this.isWatched,
    timeWatched = this.timeWatched,
    totalTime = this.totalTime,
    date = this.date,
    showId = showId,
    showImageUrl = showImageUrl,
)
