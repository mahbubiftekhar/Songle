package uk.co.iftekhar.www.songle

import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.util.Xml
import org.xmlpull.v1.XmlPullParser

class DownloadXmlTask(private val caller: MainActivity) : AsyncTask<String, Void, List<Entry>>() {

    override fun doInBackground(vararg urls: String): List<Entry> {
        return try {
            loadXmlFromNetwork(urls[0])
        } catch (e: IOException) {
            e.printStackTrace()
            /*If we encounter an issue, set downloadXML finished as false, thus the user
            * will be able to retry the download from the MapsActivity*/
            caller.DownloadXMLCompleted = false
            emptyList<Entry>()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            /* If we encounter an issue, set downloadXML finished as false, thus the user
             * will be able to retry the download from the MapsActivity*/
            caller.DownloadXMLCompleted = false
            emptyList<Entry>()
        }
    }

    private fun loadXmlFromNetwork(urlString: String): List<Entry> {
        val stream = downloadUrl(urlString)
        val XMLSongsArrayList = parse(stream)
        return XMLSongsArrayList
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

    override fun onPostExecute(result: List<Entry>) {
        super.onPostExecute(result)
        caller.downloadCompleteXML(result)
    }
}

data class Entry(val number: String, val artist: String, val title: String, val link: String)

private val ns: String? = null
@Throws(XmlPullParserException::class, IOException::class)
fun parse(input: InputStream): List<Entry> {
    //println(">>>>>> in parse()")
    input.use {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                false)
        parser.setInput(input, null)
        parser.nextTag()
        return readFeed(parser)
    }
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readFeed(parser: XmlPullParser): List<Entry> {
    //println(">>>>>> in readFeed()")
    val entries = ArrayList<Entry>()
    //println(">>>>>> in readFeed() --- require feed ")
    parser.require(XmlPullParser.START_TAG, ns, "Songs")
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        if (parser.name == "Song") {
            entries.add(readEntry(parser))
        } else {
            skip(parser)
        }
    }
    return entries
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readEntry(parser: XmlPullParser): Entry {
    parser.require(XmlPullParser.START_TAG, ns, "Song")
    var number = ""
    var title = ""
    var artist = ""
    var link = ""
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG)
            continue
        when (parser.name) {
            "Number" -> number = readNumber(parser)
            "Artist" -> artist = readArtist(parser)
            "Title" -> title = readTitle(parser)
            "Link" -> link = readLink(parser)

            else -> skip(parser)
        }
    }
    return Entry(number, artist, title, link)
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readTitle(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "Title")
    val title = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Title")
    return title
}


@Throws(IOException::class, XmlPullParserException::class)
private fun readNumber(parser: XmlPullParser): String {

    parser.require(XmlPullParser.START_TAG, ns, "Number")
    val number = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Number")
    //println(">>>>>>number: "+number)
    return number
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readLink(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "Link")
    val link = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Link")
    return link
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readArtist(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "Artist")
    val artist = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Artist")
    return artist
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readText(parser: XmlPullParser): String {

    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
private fun skip(parser: XmlPullParser) {
    if (parser.eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}