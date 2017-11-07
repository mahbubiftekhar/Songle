package uk.co.iftekhar.www.songle

/**
 * Created by MAHBUBIFTEKHAR on 06/11/2017.
 */
import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class DownloadDOC : AsyncTask<String, Void, List<List<String>>>() {

    override fun doInBackground(vararg urls: String): List<List<String>> {
        return try {
            loadXmlFromNetwork("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/04/words.txt")
        } catch (e: IOException) {
            // Always print the stacktrace!
            e.printStackTrace()
            return emptyList()
        } catch (e: XmlPullParserException) {
            // Always print the stacktrace!
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun loadXmlFromNetwork(urlString: String): List<List<String>>  {
        println("??????1")
        val stream = downloadUrl(urlString)
        println("??????2")
        val asString = StreamToString(stream)
        println("??????3")
        val SONGWORDS = parseDOC(asString)
        println("??????4")
        return SONGWORDS;
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        println(">>>>> WE ARE GETTING INTO downLoadURL")

        val url = URL(urlString)
        println(">>>>> WE ARE GETTING INTO downLoadURL2")
        val conn = url.openConnection() as HttpURLConnection
        println(">>>>> WE ARE GETTING INTO downLoadURL3")
        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.connect()
        //println(">>>>> WE ARE GETTING INTO downLoadURL4")
        return conn.inputStream
    }
    @Throws(IOException::class)
    private fun StreamToString (stream: InputStream) : String {
        println(">>>>> WE ARE GETTING INTO STREAMTOSTRING")
         val sc = Scanner(stream).useDelimiter("\\Z")
         if(sc.hasNext())
         { return sc.next()
         }
         else {
            // println(">>>>>>>>>>wearegettinghere")
             return "" }
    }

    private fun parseDOC(song: String): List<List<String>> {
    val lines = song.split("\\r?\\n".toRegex())
    val words = ArrayList<List<String>>()
    for (line in lines) {
        val splitLine = line.split("\\W+".toRegex())
        //println(">>>>>>>>>>>>>>"+splitLine)
        val TempArray = splitLine.subList(2, splitLine.lastIndex+1)
        words.add(TempArray)
    }
    println(">>>>>>>" + words)
    return words
  }

     override fun onPostExecute(result: List<List<String>>?) {
        super.onPostExecute(result)

     }

}
