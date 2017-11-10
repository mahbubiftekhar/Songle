package uk.co.iftekhar.www.songle
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_correct_splash.*
import org.jetbrains.anko.alert

class CorrectSplash : AppCompatActivity() {
    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        //super.onBackPressed()
        Send the user back to MainActivity */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

    fun networkChecker (){
        if(!isNetworkConnected()){
            Toast.makeText(this@CorrectSplash, "Check your internet connection", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    fun bulkwork(Level: String, Timed: Boolean){
        val intent = Intent(this, MapsActivity()::class.java)
        intent.putExtra("Level", Level) /*Pass the level*/
        intent.putExtra("Timed", Timed) /*Pass if timed or not*/
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_splash)

        //These should be passed by the
        val SONGYOUTUBELINK = intent.getStringExtra("SONGYOUTUBELINK")
        val SONGLYRICLINK = intent.getStringExtra("SONGLYRICLINK")
        val LEVEL = intent.getStringExtra("LEVEL") /*GET THE LEVEL NUMBER */
        YOUTUBELINK.setOnClickListener {
            networkChecker()
            val url = "https://www.youtube.com/watch?v="+(SONGYOUTUBELINK).drop(17)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        LYRICLINK.setOnClickListener {
            networkChecker()
            println("%%" + SONGLYRICLINK)
            val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SONGLYRICLINK/words.txt"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        SAMELEVEL.setOnClickListener {
            networkChecker()

            alert("Want a challenge with timed play?"){
                positiveButton("Yes, bring it on!") {
                    bulkwork(LEVEL, true)
                }
                negativeButton("No Thanks!") {
                    bulkwork(LEVEL, false)


                }
            }.show()
        }
        NEXTLEVEL.setOnClickListener {
            networkChecker()
            if(LEVEL == "5"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkwork("5", true)

                    }
                    negativeButton("No Thanks!") {
                        bulkwork("5", false)
                    }
                }.show()
            } else if (LEVEL == "4"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkwork("5", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("5", false)

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }else if (LEVEL == "3"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkwork("4", true)

                    }
                    negativeButton("No Thanks!") {
                        bulkwork("4", false)

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }
            else if (LEVEL == "2"){
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkwork("3", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("3", false)

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()

            }
            else {
                alert("Want a challenge with timed play?"){
                    positiveButton("Yes, bring it on!") {
                        bulkwork("2", true)
                    }
                    negativeButton("No Thanks!") {
                        bulkwork("2", false)

                        //Don't do anything as the user has changed there mind
                        //
                    }
                }.show()
            }
        }

    }


}
