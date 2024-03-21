package dev.niltsiar.tempopod

import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

fun main(args: Array<String>) {
    val (selectedTempo, feedUrl) = when {
        args.size > 1 -> args[0].toInt() to args[1]
        args.isNotEmpty() -> args[0].toInt() to "https://raw.githubusercontent.com/webreactiva-devs/reto-tempopod/main/feed/webreactiva.xml"
        else -> 30 to "https://raw.githubusercontent.com/webreactiva-devs/reto-tempopod/main/feed/webreactiva.xml"
    }

    val xmlContent = fetchFeed(feedUrl)
    val episodes = parseEpisodes(xmlContent)
    val selectedEpisodes = episodes.selectEpisodes(selectedTempo)

    println("Episodios seleccionados:")
    selectedEpisodes.forEach { println(it) }
}

private fun parseEpisodes(xmlContent: String): Sequence<Episode> {
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
}

private fun Sequence<Episode>.selectEpisodes(tempo: Int): Sequence<String> {
    var totalTime = 0

    return shuffled().takeWhile { episode ->
        val newTotalTime = totalTime + episode.duration
        if (newTotalTime <= tempo * 60) {
            totalTime = newTotalTime
            true
        } else {
            false
        }
    }.map { it.title }
}

private fun fetchFeed(feedUrl: String): String {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(feedUrl))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

// Extension function to convert NodeList to Sequence
private fun NodeList.asSequence(): Sequence<Node> =
    (0 until length).asSequence().map { item(it) }

data class Episode(val title: String, val duration: Int)
