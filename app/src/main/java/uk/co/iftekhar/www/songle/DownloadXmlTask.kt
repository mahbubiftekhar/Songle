package uk.co.iftekhar.www.songle

import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.util.Xml
import org.xmlpull.v1.XmlPullParser

class DownloadXmlTask() : AsyncTask<String, Void, List<Entry>>() {

    override fun doInBackground(vararg urls: String): List<Entry> {
        return try {
            loadXmlFromNetwork(urls[0])
        } catch (e: IOException) {
            // Always print the stacktrace!
            e.printStackTrace()
            emptyList<Entry>()
        } catch (e: XmlPullParserException) {
            // Always print the stacktrace!
            e.printStackTrace()
            emptyList<Entry>()
        }
    }

    private fun loadXmlFromNetwork(urlString: String): List<Entry>  {
        //System.out.println(">>>>>> THIS IS PRINTING DOWNLOADXMLTASK1")
        // val result = StringBuilder()
        //System.out.println(">>>>>> JUST BEFORE STREAM")
        val stream = downloadUrl(urlString)
        //System.out.println(">>>>>> JUST AFTER STREAM")
        //Do something with stream e.g. parse as XML, build result
        val XMLSongsArrayList = parse(stream)
        //System.out.println(">>>>>> THIS IS PRINTING DOWNLOADXMLTASK - AFTER STREAM")
        return XMLSongsArrayList;
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        //println(">>>>> in downloadUrl --- connecting to '$urlString'")
        val url = URL(urlString)
        //println(">>>>> in downloadUrl --- just before openConnection")
        val conn = url.openConnection() as HttpURLConnection
        //println(">>>>> in downloadUrl --- just after openConnection")
        // Also available: HttpsURLConnection

        //println(">>>>> in downloadUrl --- setting parameters")
        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true
        // Starts the query

        ///  println(">>>>> in downloadUrl --- just before connect()")
        conn.connect()
        // println(">>>>> in downloadUrl --- exiting and returning inputStream")
        return conn.inputStream
    }

    override fun onPostExecute(result: List<Entry>) {
        super.onPostExecute(result)
        /*Once finnished this Async task, call the map */
    }
}
data class Entry(val number: String, val artist: String, val title: String, val link: String )
private val ns: String? = null
@Throws(XmlPullParserException::class, IOException::class)
fun parse(input : InputStream): List<Entry> {
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
        //println(">>>>>>"+ "Start Tag is " + XmlPullParser.START_TAG)
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        //println(">>>>>> in readFeed() - found ${parser.name}")
        // Starts by looking for the entry tag
        //println(">>>>>>>>" + parser.name)
        if (parser.name == "Song") {
            entries.add(readEntry(parser))
        } else {
            skip(parser)
            //      println(">>>>>> skipped here in readfeed")
        }
    }
    return entries
}
@Throws(XmlPullParserException::class, IOException::class)
private fun readEntry(parser: XmlPullParser): Entry {
    //println(">>>>>> in readEntry()")

    parser.require(XmlPullParser.START_TAG, ns, "Song")
    var number = ""
    var title = ""
    var artist = ""
    var link = ""
    while (parser.next() != XmlPullParser.END_TAG) {
        //println(">>>>>"+parser.name)
        if (parser.eventType != XmlPullParser.START_TAG)
            continue
        when(parser.name){
            "Number" -> number = readNumber(parser)
            "Artist" -> artist = readArtist(parser)
            "Title" -> title = readTitle(parser)
            "Link" -> link = readLink(parser)

            else -> skip(parser)
        }
    }
    //println(">>>>>>"+number+ artist+ title+ link)
    return Entry(number, artist, title, link)
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readTitle(parser: XmlPullParser): String {
    //println(">>>>>> in readTitle()")
    parser.require(XmlPullParser.START_TAG, ns, "Title")
    val title = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Title")
    //println(">>>>>>title: "+title)
    return title
}


@Throws(IOException::class, XmlPullParserException::class)
private fun readNumber(parser: XmlPullParser): String {
    //println(">>>>>> in readNumber()")

    parser.require(XmlPullParser.START_TAG, ns, "Number")
    val number = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Number")
    //println(">>>>>>number: "+number)
    return number
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readLink(parser: XmlPullParser): String {
    //println(">>>>>> in readLink()")
    parser.require(XmlPullParser.START_TAG, ns, "Link")
    var link = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Link")
    //println(">>>>>>link: "+link)
    return link
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readArtist(parser: XmlPullParser): String {
    //println(">>>>>> in readArtist()")

    parser.require(XmlPullParser.START_TAG, ns, "Artist")
    val artist = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "Artist")
    //println(">>>>>>artist: "+artist)
    return artist
}
@Throws(IOException::class, XmlPullParserException::class)
private fun readText(parser: XmlPullParser): String {
    //println(">>>>>> in readText()")

    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}
@Throws(XmlPullParserException::class, IOException::class)
private fun skip(parser: XmlPullParser) {
    //println(">>>>>> in skip()")

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