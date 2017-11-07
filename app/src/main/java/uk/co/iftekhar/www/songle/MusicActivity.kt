package uk.co.iftekhar.www.songle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.content.Intent
import android.net.Uri
import android.R.attr.button




class MusicActivity : AppCompatActivity() {
    fun downloadCompleteY(result: List<EntryK>) {
        val NumberOfSongs_music = result.size
        val SongTitles_music = arrayOfNulls<String>(NumberOfSongs_music)
        val SongLinks_music = arrayOfNulls<String>(NumberOfSongs_music)

        for(i in 0..NumberOfSongs_music-1) {
            SongTitles_music[i] = result[i].title
            SongLinks_music[i] = result[i].link
        }
        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout) //as LinearLayout

        for (i in 0..NumberOfSongs_music-1) { /*creates enough buttons for each song, and says what the buttons do */
            val button_dynamic = Button(this)
            // setting layout_width and layout_height using layout parameters
            button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button_dynamic.text = SongTitles_music.get(i)
            val url = "https://www.youtube.com/watch?v="+ (SongLinks_music[i])!!.drop(17)
            button_dynamic.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }

            ll_main.addView(button_dynamic) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        val XMLSONGS = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml" //link for xml file, will not change - if it does im screwed
        val Songs = DownloadY(this)
        Songs.execute(XMLSONGS) // Run XML async task






    }


}