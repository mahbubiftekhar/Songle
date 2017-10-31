package uk.co.iftekhar.www.songle

import android.content.res.Resources
import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import javax.net.ssl.HttpsURLConnection

class DownloadKmlTask() : AsyncTask<String, Void, List<EntryKml>>() {

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

    private fun loadKMLFromNetwork(urlString: String): List<EntryKml>  {
        val stream = downloadUrl(urlString)
        val KMLSongsArrayList = parseKML(stream)
        return KMLSongsArrayList;
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        println(">>>>> in downloadUrl --- setting parameters")
        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true
        // Starts the query

        println(">>>>> in downloadUrl --- just before connect()")
        conn.connect()
        println(">>>>> in downloadUrl --- exiting and returning inputStream")
        return conn.inputStream
    }

    override fun onPostExecute(result: List<EntryKml>) {
        super.onPostExecute(result)
    }
}
data class EntryKml(val name: String, val description: String, val styleUrl: String, val Point: String )

private val ns: String? = null
@Throws(XmlPullParserException::class, IOException::class)
fun parseKML(input : InputStream): List<EntryKml> {
    println(">>>>>> in parse()")
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
    println(">>>>>> in readFeed()")
    val entrieskml = ArrayList<EntryKml>()
    println(">>>>>> in readFeed() --- require feed ")
    ParserKML.require(XmlPullParser.START_TAG, ns, "kml")
    while (ParserKML.next() != XmlPullParser.END_TAG) {
        println(">>>>>>"+ "Start Tag is " + XmlPullParser.START_TAG)
        if (ParserKML.eventType != XmlPullParser.START_TAG) {
            continue
        }
        println(">>>>>> in readFeed() - found ${ParserKML.name}")
        // Starts by looking for the EntryKml tag
        println(">>>>>>>>" + ParserKML.name)
        if (ParserKML.name == "Document") {
            entrieskml.add(readEntryKml(ParserKML))
        } else {
            skip(ParserKML)
                 println(">>>>>> skipped here in readfeed")
        }
    }
    return entrieskml
}
@Throws(XmlPullParserException::class, IOException::class)
private fun readEntryKml(ParserKML: XmlPullParser): EntryKml {
    println(">>>>>> in readEntryKml()")
    ParserKML.require(XmlPullParser.START_TAG, ns, "Document")
    var name = ""
    var description = ""
    var styleUrl = ""
    var Point = ""
    while (ParserKML.next() != XmlPullParser.END_TAG) {
        println(">>>>>" + ParserKML.name)
        if (ParserKML.eventType != XmlPullParser.START_TAG)
            continue
        if (ParserKML.name == "Placemark")
            continue
        when(ParserKML.name){
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
    println(">>>>>> in readName()")
    ParserKML.require(XmlPullParser.START_TAG, ns, "name")
    val name = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "name")
    println(">>>>>>name: "+name)
    return name
}


@Throws(IOException::class, XmlPullParserException::class)
private fun readDescription(ParserKML: XmlPullParser): String {
    println(">>>>>> in readDescription()")
    ParserKML.require(XmlPullParser.START_TAG, ns, "description")
    val description = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "description")
    println(">>>>>>description: "+description)
    return description
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readPoint(ParserKML: XmlPullParser): String {
    println(">>>>>> in readPoint()")
    //ParserKML.require(XmlPullParser.START_TAG, ns, "Point")
    var Point = "" //readcoordinates(ParserKML)
    //ParserKML.require(XmlPullParser.END_TAG, ns, "Point")
    println(">>>>>>Point: "+Point)
    return Point
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readcoordinates(ParserKML: XmlPullParser): String {
    println(">>>>>> in readreadcoordinates()")
    ParserKML.require(XmlPullParser.START_TAG, ns, "coordinates")
    var coordinates = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "coordinates")
    println(">>>>>>coordinates: "+coordinates)
    return coordinates
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readStyleUrl(ParserKML: XmlPullParser): String {
    println(">>>>>> in readStyleUrl()")

    ParserKML.require(XmlPullParser.START_TAG, ns, "styleUrl")
    val StyleUrl = readText(ParserKML)
    ParserKML.require(XmlPullParser.END_TAG, ns, "styleUrl")
    println(">>>>>>StyleUrl: "+StyleUrl)
    return StyleUrl
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readText(ParserKML: XmlPullParser): String {
    //println(">>>>>> in readText()")
    var result = ""
    if (ParserKML.next() == XmlPullParser.TEXT) {
        result = ParserKML.text
        ParserKML.nextTag()
    }
    return result
}
@Throws(XmlPullParserException::class, IOException::class)
private fun skip(ParserKML: XmlPullParser) {
    println(">>>>>> in skip()")
    if (ParserKML.eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (ParserKML.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}