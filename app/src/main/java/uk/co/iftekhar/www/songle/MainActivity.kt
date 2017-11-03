package uk.co.iftekhar.www.songle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_BLUETOOTH
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_VPN
import android.net.ConnectivityManager.TYPE_WIFI
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Arrays.asList




class MainActivity : AppCompatActivity() {
    var networkPref = "any" /* Have preset network communication as any */

    private inner class NetworkReceiver : BroadcastReceiver() { /*network receiver - from slidse */
        override fun onReceive(context: Context, intent: Intent) {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkPref == "wifi" && networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
                // Wi´Fi is connected, so use Wi´Fi
            } else if (networkPref == "any" && networkInfo != null) {

                // Have a network connection and permission, so use data
            } else {
                // No Wi´Fi and no permission, or no network connection
            }
        }
    }
    private var receiver = NetworkReceiver()
    val SPLASH_SCREEN_TIME = 4000 /* time to show splashscreen */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //System.out.println("THIS IS PRINTING")
        val XMLSONGS = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml" /* The link for downloading the XML with song information */
        val XMLSongs = DownloadXmlTask()
        val Parsed = XMLSongs.execute(XMLSONGS)
        var numberofsongs = (Parsed.get().lastIndex) + 1 /*Number of songs in XML, please note 0 is the start */

        fun Random.nextInt(range: IntRange): Int { /*creates a random number */
            return range.start + nextInt(range.last - range.start)
        }

        fun bulkWord (Level: String) {
            var URLNUMBER = "";
            val random = Random()
            val RandomNumberinRange = random.nextInt(1..numberofsongs) /*Random number in range for the button to open */
            if(RandomNumberinRange < 10) {
                URLNUMBER = "0" + (RandomNumberinRange).toString()
            } else {
                URLNUMBER = RandomNumberinRange.toString()
            }
            println(">>>>>>>> SONG NUMBER IS " + URLNUMBER)
            val KMLMAPSURL = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/"+URLNUMBER+"/map"+Level+".kml"
            val KMLmap = DownloadKmlTask()
            val KMLParsed = KMLmap.execute(KMLMAPSURL)
            val numberofPoints = KMLParsed.get().lastIndex;

            val PointsLong = arrayOfNulls<String>(numberofPoints+1)
            val PointsLat = arrayOfNulls<String>(numberofPoints+1)
            val classification = arrayOfNulls<String>(numberofPoints+1)
            val name = arrayOfNulls<String>(numberofPoints+1)
            val SongTitles = arrayOfNulls<String>(numberofsongs+1)
            val SongLinks = arrayOfNulls<String>(numberofsongs+1)
            for (i in 0..numberofsongs-1) {
                val a = Parsed.get()[i].link
                SongLinks[i] = a
            }
            SongTitles[0] = "Guess the song!!"
            for (i in 1..numberofsongs) {
                val b = Parsed.get()[i-1].title
                SongTitles[i] = b
            }
            for (i in 0..numberofPoints) {
                val a = KMLParsed.get()[i].Point
                val input = a
                var result: List<String> = input.split(",").map { it.trim() } /*String into List  */
                PointsLong[i] = result.get(0)
                PointsLat[i] = result.get(1)
                val b = KMLParsed.get()[i].description
                classification[i] = b;
                val c = KMLParsed.get()[i].name
                name[i] = c;
            }
            val THESONGNAME = SongTitles[RandomNumberinRange];
            val THESONGLINK = SongLinks[RandomNumberinRange];
            println("£££££££"+THESONGNAME);
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("numberofmarkers", numberofPoints) /*passing number of markers */
            intent.putExtra("PointsLat", PointsLat)
            intent.putExtra("PointsLong", PointsLong)
            intent.putExtra("classification", classification)
            intent.putExtra("name", name)
            intent.putExtra("SongLinks", SongLinks)
            intent.putExtra("SongTitles", SongTitles)
            intent.putExtra("THESONGNAME", THESONGNAME)
            intent.putExtra("THESONGLINK", THESONGLINK)
            startActivity(intent)

        }
        LEVEL1_BUTTON.setOnClickListener {
            bulkWord("5") /*Easiest, most words with lots of classificatins */
        }
        LEVEL2_BUTTON.setOnClickListener {
            bulkWord("4")

        }
        LEVEL3_BUTTON.setOnClickListener {
            bulkWord("3")

        }
        LEVEL4_BUTTON.setOnClickListener {
            bulkWord("2")

        }
        LEVEL5BUTTON.setOnClickListener {
            bulkWord("1")
        /*Hardest, least words with no classificatins */
        }
        MUSICBUTTON.setOnClickListener{
                val SongLinks = arrayOfNulls<String>(numberofsongs)
                val SongTitles = arrayOfNulls<String>(numberofsongs)
                for (i in 0..numberofsongs-1) {

                    val a = Parsed.get()[i].link
                    SongLinks[i] = a
                    val b = Parsed.get()[i].title
                    SongTitles[i] = b
                }
                val intent = Intent(this, MusicActivity::class.java)
                intent.putExtra("NumberOfSongs", numberofsongs)
                intent.putExtra("SongLinks", SongLinks)
                intent.putExtra("SongTitles", SongTitles)
                startActivity(intent)
            }

            SONGLYRICS.setOnClickListener {
            val SongTitles = arrayOfNulls<String>(numberofsongs)
            for (i in 0..numberofsongs-1) {
                val b = Parsed.get()[i].title
                SongTitles[i] = b
            }
            val intent = Intent(this, SongLyricActivity::class.java)
            intent.putExtra("NumberOfSongs", numberofsongs)
            intent.putExtra("SongTitles", SongTitles)
            startActivity(intent)
            }
            HOWTOPLAY.setOnClickListener {

            }
    }
}



