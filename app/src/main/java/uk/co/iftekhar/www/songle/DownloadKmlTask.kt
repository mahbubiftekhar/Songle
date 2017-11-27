package uk.co.iftekhar.www.songle

import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.util.Xml
import org.xmlpull.v1.XmlPullParser

class DownloadKmlTask(private val caller: MapsActivity) : AsyncTask<String, Void, List<EntryKml>>() {
    override fun doInBackground(vararg urls: String): List<EntryKml> {
        return try {
            loadKMLFromNetwork(urls[0])
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList<EntryKml>()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            emptyList<EntryKml>()
        }
    }

    private fun loadKMLFromNetwork(urlString: String): List<EntryKml> {
        val stream = downloadUrl(urlString)
        val KMLSongsArrayList = parseKML(stream)
        return KMLSongsArrayList
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.connect()
        return conn.inputStream
    }

    override fun onPostExecute(result: List<EntryKml>) {
        super.onPostExecute(result)
        caller.downloadCompleteKML(result)

    }
}

data class EntryKml(val name: String, val description: String, val styleUrl: String, val Point: String)

private val ns: String? = null
@Throws(XmlPullParserException::class, IOException::class)
fun parseKML(input: InputStream): List<EntryKml> {
    input.use {
        val ParserKML = Xml.newPullParser()
        ParserKML.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                false)
        ParserKML.setInput(input, null)
        ParserKML.nextTag()
        return readFeed(ParserKML)
    }
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readFeed(ParserKML: XmlPullParser): List<EntryKml> {
    val entrieskml = ArrayList<EntryKml>()
    ParserKML.require(XmlPullParser.START_TAG, ns, "kml")
    while (ParserKML.next() != XmlPullParser.END_TAG) {
        if (ParserKML.eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (ParserKML.name == "Document") {
            entrieskml.add(readEntryKml(ParserKML))
        } else if (ParserKML.name == "Placemark") {
            entrieskml.add(readEntryKml2(ParserKML))

        } else {
            skip(ParserKML)
            println(">>>>>> skipped here in readfeed")
        }
    }
    return entrieskml
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readEntryKml(ParserKML: XmlPullParser): EntryKml {
    ParserKML.require(XmlPullParser.START_TAG, ns, "Document")
    var name = ""
    var description = ""
    var styleUrl = ""
    var Point = ""
    while (ParserKML.next() != XmlPullParser.END_TAG) {
        if (ParserKML.eventType != XmlPullParser.START_TAG)
            continue
        if (ParserKML.name == "Placemark")
            continue
        when (ParserKML.name) {
            "name" -> name = readName(ParserKML)
            "description" -> description = readDescription(ParserKML)
            "styleUrl" -> styleUrl = readStyleUrl(ParserKML)
            "Point" -> Point = readPoint(ParserKML)
            else -> skip(ParserKML)
        }
    }
    return EntryKml(name, description, styleUrl, Point)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readEntryKml2(ParserKML: XmlPullParser): EntryKml {
    ParserKML.require(XmlPullParser.START_TAG, ns, "Placemark")
    var name = ""
    var description = ""
    var styleUrl = ""
    var Point = ""
    while (ParserKML.next() != XmlPullParser.END_TAG) {
        if (ParserKML.eventType != XmlPullParser.START_TAG)
            continue
        if (ParserKML.name == "Placemark")
            continue
        when (ParserKML.name) {
            "name" -> name = readName(ParserKML)
            "description" -> description = readDescription(ParserKML)
            "styleUrl" -> styleUrl = readStyleUrl(ParserKML)
            "Point" -> Point = readPoint(ParserKML)
            else -> skip(ParserKML)
        }
    }
    return EntryKml(name, description, styleUrl, Point)
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readName(ParserKML: XmlPullParser): String {
    ParserKML.require(XmlPullParser.START_TAG, ns, "name")
    val name = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "name")
    return name
}


@Throws(IOException::class, XmlPullParserException::class)
private fun readDescription(ParserKML: XmlPullParser): String {
    ParserKML.require(XmlPullParser.START_TAG, ns, "description")
    val description = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "description")
    return description
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readPoint(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "Point")
    var coords = ""
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        val theName = parser.name
        if (theName == "coordinates") {
            coords = readCoordinates(parser)
        } else {
            skip(parser)
        }
    }
    return coords
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readCoordinates(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "coordinates")
    val coordinates = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "coordinates")
    return coordinates
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readStyleUrl(ParserKML: XmlPullParser): String {
    ParserKML.require(XmlPullParser.START_TAG, ns, "styleUrl")
    val StyleUrl = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "styleUrl")
    return StyleUrl
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readText(ParserKML: XmlPullParser): String {
    var result = ""
    if (ParserKML.next() == XmlPullParser.TEXT) {
        result = ParserKML.text
        ParserKML.nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
private fun skip(ParserKML: XmlPullParser) {
    if (ParserKML.eventType != XmlPullParser.START_TAG) {
    }
    var depth = 1
    while (depth != 0) {
        when (ParserKML.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}