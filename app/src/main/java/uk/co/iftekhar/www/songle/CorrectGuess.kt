package uk.co.iftekhar.www.songle

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

class CorrectGuess : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_guess)

        val button_dynamic = Button(this)
        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout)
        button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
       // button_dynamic.text = SongTitles_music.get(IndexCount)

        //val url = "https://www.youtube.com/watch?v="+(SongLinks_music[IndexCount]).drop(17)
        button_dynamic.setOnClickListener {
          //  startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        ll_main.addView(button_dynamic) }

}
