package dev.niltsiar.tempopod

import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.function.Consumer
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

fun main(args: Array<String>) {
    val selectedTempo = if (args.size > 0) args[0].toInt() else 30
    val feedUrl = if (args.size > 1) args[1] else "https://raw.githubusercontent.com/webreactiva-devs/reto-tempopod/main/feed/webreactiva.xml"

    val xmlContent = fetchFeed(feedUrl)
    val episodes = parseEpisodes(xmlContent)
    val selectedEpisodes = selectEpisodes(episodes, selectedTempo)
    println("Episodios seleccionados:")
    selectedEpisodes.forEach(Consumer { x: String? -> println(x) })
}

private fun fetchFeed(feedUrl: String): String {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(feedUrl))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

private fun parseEpisodes(xmlContent: String): List<Episode> {
    val document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(InputSource(StringReader(xmlContent)))

    return document.getElementsByTagName("item")
        .asSequence() // Convert NodeList to Sequence for more idiomatic Kotlin
        .filterIsInstance<Element>() // Filter out non-Element nodes
        .map { element ->
            val title = element.getElementsByTagName("title").item(0).textContent
            val durationStr = element.getElementsByTagName("itunes:duration").item(0).textContent
            val duration = durationStr.toInt()
            Episode(title, duration)
        }
        .toList() // Convert Sequence back to List
}

// Extension function to convert NodeList to Sequence
private fun NodeList.asSequence(): Sequence<Node> =
    (0 until length).asSequence().map { item(it) }

private fun selectEpisodes(episodes: List<Episode>, tempo: Int): List<String> {
    val shuffledEpisodes = episodes.shuffled()
    var totalTime = 0

    val selectedEpisodes = shuffledEpisodes.takeWhile { episode ->
        val newTotalTime = totalTime + episode.duration
        if (newTotalTime <= tempo * 60) {
            totalTime = newTotalTime
            true
        } else {
            false
        }
    }

    return selectedEpisodes.map { it.title }
}

data class Episode(val title: String, val duration: Int)
