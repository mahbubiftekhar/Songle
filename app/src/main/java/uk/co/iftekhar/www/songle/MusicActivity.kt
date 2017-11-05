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
    override fun onCreate(savedInstanceState: Bundle?) {
        val NumberOfSongs_music = intent.getIntExtra("NumberOfSongs",0) /*receives the data from ActivityMain*/
        val SongLinks_music = intent.getStringArrayExtra("SongLinks")
        val SongTitles_music = intent.getStringArrayExtra("SongTitles")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout) //as LinearLayout

        // creating the buttons dynamically
        var IndexCount = 0
        for (i in 0..NumberOfSongs_music-1) { /*creates enough buttons for each song, and says what the buttons do */
            val button_dynamic = Button(this)
        // setting layout_width and layout_height using layout parameters
        button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        button_dynamic.text = SongTitles_music.get(IndexCount)
            val url = "https://www.youtube.com/watch?v="+(SongLinks_music[IndexCount]).drop(17)
            button_dynamic.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            IndexCount++

        ll_main.addView(button_dynamic) }
    }

}