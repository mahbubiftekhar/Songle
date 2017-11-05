package uk.co.iftekhar.www.songle
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_correct_splash.*
import org.jetbrains.anko.alert
import java.util.*

class CorrectSplash : AppCompatActivity() {
    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user
        can go back to previous maps and we do not want that*/
        super.onBackPressed()
        /*Send the user back to MainActivity */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

    fun networkChecker (){
        if(isNetworkConnected()==false){
            Toast.makeText(this@CorrectSplash, "Check your internet connection", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_splash)
        fun Random.nextInt(range: IntRange): Int { /*creates a random number */
            return range.start + nextInt(range.last - range.start)
        }


        fun bulkWord (Level: String, Timed: Boolean) {
            val XMLSONGS = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml" /* The link for downloading the XML with song information */
            val XMLSongs = DownloadXmlTask()
            val Parsed = XMLSongs.execute(XMLSONGS)
            var numberofsongs = (Parsed.get().lastIndex) + 1 /*Number of songs in XML, please note 0 is the start */

            var URLNUMBER = ""
            val random = Random()
            val RandomNumberinRange = random.nextInt(1..numberofsongs) /*Random number in range for the button to open */
            if(RandomNumberinRange < 10) {
                URLNUMBER = "0" + (RandomNumberinRange).toString()
            } else {
                URLNUMBER = RandomNumberinRange.toString()
            }
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
                PointsLong[i] = result[0]
                PointsLat[i] = result[1]
                val b = KMLParsed.get()[i].description
                classification[i] = b
                val c = KMLParsed.get()[i].name
                name[i] = c
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
            intent.putExtra("Timed",Timed)

            //These are used after the user has guessed the songs
            intent.putExtra("CURRENTLEVEL", Level)
            intent.putExtra("SONGLINKYOUTUBE",SongLinks[RandomNumberinRange]) //Pass song link Youtube
            intent.putExtra("SONGLINKLYRIC",SongTitles[RandomNumberinRange]) // Pass song link Lyrics

            startActivity(intent)

        }
        val SONGYOUTUBELINK = intent.getStringExtra("SONGYOUTUBELINK")
        val SONGLYRICLINK = intent.getStringExtra("SONGLYRICLINK")
        val LEVEL = intent.getStringExtra("LEVEL") /*GET THE LEVEL NUMBER */
        println("!!!!!" + LEVEL);
        YOUTUBELINK.setOnClickListener {
            networkChecker()
            //val YOUTUBELINK = Button(this)
            val url = "https://www.youtube.com/watch?v="+(SONGYOUTUBELINK).drop(17)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        LYRICLINK.setOnClickListener {
            networkChecker()
            println("%%" + SONGLYRICLINK)
            val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/" + SONGLYRICLINK + "/words.txt"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        SAMELEVEL.setOnClickListener {
            networkChecker()

            alert("Want a challenge with timed play?"){
                positiveButton("Yes, bring it on!") {
                    bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                }
                negativeButton("No Thanks!") {
                    bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                    //Don't do anything as the user has changed there mind
                    //
                }
            }.show()
        }
        NEXTLEVEL.setOnClickListener {
            networkChecker()
            if(LEVEL == "5"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                    }
                    negativeButton("No Thanks!") {
                        bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            } else if (LEVEL == "4"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                    }
                    negativeButton("No Thanks!") {
                        bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }else if (LEVEL == "3"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                    }
                    negativeButton("No Thanks!") {
                        bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }
            else if (LEVEL == "2"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                    }
                    negativeButton("No Thanks!") {
                        bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()

            }
            else {
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkWord(LEVEL, true) /*Easiest, most words with lots of classificatins */
                    }
                    negativeButton("No Thanks!") {
                        bulkWord(LEVEL,false) /*Easiest, most words with lots of classificatins */

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }
        }

    }
}
