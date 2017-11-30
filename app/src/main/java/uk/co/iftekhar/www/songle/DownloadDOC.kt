package uk.co.iftekhar.www.songle

import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class DownloadDOC(private val caller: MapsActivity) : AsyncTask<String, Void, List<List<String>>>() {

    override fun doInBackground(vararg urls: String): List<List<String>> {
        return try {
            loadXmlFromNetwork("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0" + urls[0] + "/words.txt")
        } catch (e: IOException) {
            e.printStackTrace()
            caller.downloadDOCFinnished = false
            return emptyList()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            caller.downloadDOCFinnished = false
            return emptyList()
        }
    }

    private fun loadXmlFromNetwork(urlString: String): List<List<String>> {
        val stream = downloadUrl(urlString)
        val asString = StreamToString(stream)
        val SONGWORDS = parseDOC(asString)
        return SONGWORDS
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

    @Throws(IOException::class)
    private fun StreamToString(stream: InputStream): String {
        val sc = Scanner(stream).useDelimiter("\\Z")
        if (sc.hasNext()) {
            return sc.next()
        } else {
            return ""
        }
    }

    private fun parseDOC(song: String): List<List<String>> {
        val lines = song.split("\\r?\\n".toRegex()) //first split on new lines
        val words = ArrayList<List<String>>()
        for (line in lines) { // then we need to split further
            val splitLine = line.split("[^a-zA-Z0-9'-]+".toRegex())
            val TempArray = splitLine.subList(2, splitLine.lastIndex + 1)
            words.add(TempArray)
        }
        return words
    }

    override fun onPostExecute(result: List<List<String>>?) {
        super.onPostExecute(result)
        caller.downloadCompletDOC(result)
    }

}
