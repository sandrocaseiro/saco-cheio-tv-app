package dev.sandrocaseiro.sacocheiotv.services

import android.util.Log
import dev.sandrocaseiro.sacocheiotv.models.api.AEpisode
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisodeMedia
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiService {
    private val client = HttpClient()

//    https://sacocheio.tv/podcast/saco-cheio/__data.json?x-sveltekit-invalidated=11

    private suspend fun getEpisodeInfo(show: String, episodeSlug: String): Map<String, Any> {
        val resp = client.get("https://sacocheio.tv/podcast/$show/$episodeSlug/__data.json") {
            cookie("auth_session", AUTH)
        }

        val responseData = resp.bodyAsText()
        Log.d(TAG, responseData)
        val content = Json.parseToJsonElement(responseData)

        val attrs = mutableMapOf<Int, String>()
        val episodeData = mutableMapOf<String, Any>()
//        TODO: Check this logic
        var currentIndex = 2
        content.jsonObject["nodes"]!!.jsonArray[1].jsonObject["data"]!!.jsonArray.forEach {
            if (it is JsonObject) {
                //                Log.d(TAG, it.jsonObject.toString())
                for (entry in it.jsonObject.entries) {
                    attrs[entry.value.jsonPrimitive.content.toInt()] = entry.key
                }
            } else {
//                Log.d(TAG, it.jsonPrimitive.toString())
                episodeData[attrs[currentIndex]!!] = it.jsonPrimitive.content
                currentIndex++
            }
        }

        Log.d(TAG, attrs.toString())
        Log.d(TAG, episodeData.toString())

        return episodeData
    }

    private suspend fun getVideoUrls(videoPlaylistUrl: String): Map<String, String> {
        val resp = client.get(videoPlaylistUrl) {
            header("referer", "https://sacocheio.tv/")
        }

        val videoUrls = mutableMapOf<String, String>()
        resp.bodyAsText().substringAfter("#EXT-X-STREAM-INF:")
            .split("#EXT-X-STREAM-INF:").forEach {
                val quality = it
                    .substringAfter("RESOLUTION=")
                    .substringAfter("x")
                    .replace("\r", "")
                    .substringBefore("\n") + "p"
                val videoUrl = videoPlaylistUrl
                    .substringBeforeLast("/") + "/" + it.substringAfter("\n").substringBefore("\n")
                videoUrls[quality] = videoUrl
            }

        Log.d(TAG, videoUrls.toString())
        return videoUrls
    }

    suspend fun getEpisodes(show: String): List<AEpisode> {
        val eps = mutableListOf<AEpisode>()
        val resp = client.get("https://sacocheio.tv/podcast/$show") {
            cookie("auth_session", AUTH)
        }

        val rssSource = resp.bodyAsText()
        Log.d(TAG, rssSource)
        var regexResult = ((Regex(
            "const data = (?<script>(.|\\n|\\t|\\r)*?)];",
            setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
        )
            .find(rssSource)?.groups?.get("script")?.value ?: "[") + "]")
        regexResult = Regex(
            "(?:,|\\{)(\\w+)(?<!\\bhttps):",
            setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
        )
            .replace(regexResult) {
                it.value.replace(
                    it.groupValues[1],
                    "\"${it.groupValues[1]}\""
                )
            }
        regexResult =
            Regex("new Date\\((\\d+)\\)", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
                .replace(regexResult) { it.groupValues[1] }

        Log.d(TAG, regexResult)
        val content = Json.parseToJsonElement(regexResult)

        content.jsonArray[1].jsonObject["data"]!!.jsonObject["episodes"]!!.jsonArray.forEach {
            val ep = AEpisode(
                id = it.jsonObject["id"]!!.jsonPrimitive.content.toInt(),
                title = it.jsonObject["title"]!!.jsonPrimitive.content.trim(),
                description = it.jsonObject["description"]!!.jsonPrimitive.content,
                slug = it.jsonObject["slug"]!!.jsonPrimitive.content,
                showSlug = it.jsonObject["showSlug"]!!.jsonPrimitive.content,
                date = it.jsonObject["postDate"]!!.jsonPrimitive.content,
                imageUrl = it.jsonObject["thumbnailUrl"]!!.jsonPrimitive.content,
            )
            eps.add(ep)
        }

        return eps
    }

    suspend fun getEpisodeMediaUrls(show: String, episodeSlug: String): Map<VEpisodeMedia, String> {
        val media = mutableMapOf<VEpisodeMedia, String>()
        val episodeInfo = getEpisodeInfo(show, episodeSlug)
        if (episodeInfo[EPISODE_VIDEO_URL] != null && episodeInfo[EPISODE_VIDEO_URL] != "") {
            val videoUrls = getVideoUrls(episodeInfo[EPISODE_VIDEO_URL].toString())
            if (videoUrls["1080p"] != null)
                media[VEpisodeMedia.VIDEO] = videoUrls["1080p"]!!
            else if (videoUrls["720p"] != null)
                media[VEpisodeMedia.VIDEO] = videoUrls["720p"]!!
            else
                media[VEpisodeMedia.VIDEO] = videoUrls["480p"]!!
        }

        if (episodeInfo[EPISODE_AUDIO_URL] != null && episodeInfo[EPISODE_AUDIO_URL] != "") {
            media[VEpisodeMedia.AUDIO] = episodeInfo[EPISODE_AUDIO_URL].toString()
        }

        Log.d(TAG, "Media info: $media")

        return media
    }

    companion object {
        private const val TAG = "ApiService"
        private const val AUTH = "1234"

        private const val EPISODE_VIDEO_URL = "urlMp4"
        private const val EPISODE_AUDIO_URL = "urlMp3"
    }
}
