package uk.co.iftekhar.www.songle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import android.os.Vibrator
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.alert
import java.util.Timer
import java.util.Random
import kotlin.concurrent.timerTask


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var RandomNumberinRange = 0 /* defining RandomNumberInRange to allow both AsyncTasks to access it. */
    var YoutubeLinkOfCurrentSong = ""
    var LyricLinkOfCurrentSong = ""
    private var numberOfTriesLeft = 5
    var downloadDOCFinnished = true
    var downloadKMLFinnished = true
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    //var mLocationPermissionGranted = false
    private var mLastLocation: Location? = null
    //val TAG = "MapsActivity"
    lateinit var Spinner: Spinner
    lateinit var result: TextView
    val timer = Timer()
    var NumberOfMarkers = 0
    val markersformap: MutableList<Marker> = arrayListOf()
    var words: List<List<String>> = arrayListOf()
    var FindClosestMarker = false
    private var start = 0L
    var numberofsongs = 0
    lateinit var SongTitles: Array<String?>
    lateinit var SongLinks: Array<String?>

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun networkChecker() {
        /* Function to check if network is available - using isNetworkConnected -
        if network is not available, the user will be taken to the network not available
        splash screen */
        if (!isNetworkConnected()) {
            /*If there is a network issue send user to network issue page */
            val intent = Intent(this, NetworkIssue()::class.java)
            startActivity(intent)
        }
    }

    private fun launchKMLDownload(SongNumber: String, LEVEL1: String) {
        downloadKMLFinnished = true
        val KMLMAPSURL = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$SongNumber/map$LEVEL1.kml"
        val KMLmap = DownloadKmlTask(this)
        KMLmap.execute(KMLMAPSURL)
    }

    private fun launchDOCdownload(SongNumber: String) {
        downloadDOCFinnished = true
        println("HERE IN launchDOCdonwload")
        val WordsDoc = DownloadDOC(this) /* Execute the Async task for downloading and parsing the words*/
        WordsDoc.execute(SongNumber)
    }

    fun vibrate() {
        if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(300, 10))
        } else {
            /*for backward comparability*/
            @Suppress("DEPRECATION")
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
        }
    }

    fun LoadString(key: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getString(key, "STANDARD")
        return savedValue
    }

    fun SaveInt(key: String, value: Int) {
        /*Function to save a shared preference int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun SaveLong(key: String, value: Long) {
        /*Function to save a shared preference long*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun LoadInt(key: String): Int {
        /*Function to load a shared preference int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }

    fun LoadLong(key: String): Long {
        /*Function to load a shared preference long*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getLong(key, 0.toLong())
        return savedValue
    }

    fun startTimer(seconds: Long) {
        /* Start timer if the user wants to do timed play give the user notice of how long they have to play the game */
        Toast.makeText(this@MapsActivity, "You have ${seconds / 60000} minutes! GOOD LUCK!!", Toast.LENGTH_LONG).show()
        vibrate()
        timer.schedule(timerTask {
            /*Change activity if user fails to complete task in given time
             * Below we are editing the RandomNumberInRange and calling incorrectGuess with the
              * randomNumberInRange in the require format e.g. 9 -> 09*/
            if (RandomNumberinRange < 10) {
                incorrectguess("0" + LyricLinkOfCurrentSong)
            } else {
                incorrectguess(LyricLinkOfCurrentSong)
            }
        }, seconds)
    }

    private fun endTimer() { /* function to end the timer */
        timer.cancel()
    }

    fun switchBackToMain() { /*function to change activity to main activity*/
        endTimer() /*end the timer */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() { /* override the back button, so they user realises they will end the game */
        alert("End the game?") {
            positiveButton("Yes, end game") {
                switchBackToMain()
                /*Switch user to the main screen and end the game */
            }
            negativeButton("No! Stay!") {
                /*Do nothing, the user changed their minds. */
            }
        }.show()
    }

    fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
    fun correctGuess(LYRICLINK: String?) {
        endTimer()
        val LEVEL = intent.getStringExtra("Level")
        val timedBonusVALUE = LoadInt("TIMED_BONUS")
        val TIMER = intent.getBooleanExtra("Timed", false)
        FindClosestMarker = false /* So no new markers are collected */
        if (LEVEL.toInt() == 5) {
            if (TIMER) {
                /*If the user has successfully completed timed, update Timed bonus appropriately*/
                SaveInt("TIMED_BONUS", timedBonusVALUE + 1)
            }
            val a = LoadInt("EASY_LEVEL") /*Get previous number of successes*/
            SaveInt("EASY_LEVEL", a + 1) /*Increment and save number of successes*/
            val time: Long = (System.currentTimeMillis() - start) / 1000
            val sd = LoadLong("BEST_TIME_EASY")
            if (time < sd || sd == 0.toLong()) {
                /*Update best time if the previous best time was beat*/
                SaveLong("BEST_TIME_EASY", time)
            }
        } else if (LEVEL.toInt() == 4) {
            if (TIMER) {
                /*If the user has successfully completed timed, update Timed bonus appropriately*/
                SaveInt("TIMED_BONUS", timedBonusVALUE + 2)
            }
            val a = LoadInt("NORMAL_LEVEL")
            SaveInt("NORMAL_LEVEL", a + 1)
            val time = (System.currentTimeMillis() - start) / 1000
            val sd = LoadLong("BEST_TIME_NORMAL")
            if (time < sd || sd == 0.toLong()) {
                /*Update best time if the previous best time was beat*/
                SaveLong("BEST_TIME_NORMAL", time)
            }
        } else if (LEVEL.toInt() == 3) {
            if (TIMER) {
                /*If the user has successfully completed timed, update Timed bonus appropriately*/
                SaveInt("TIMED_BONUS", timedBonusVALUE + 3)
            }
            val a = LoadInt("HARD_LEVEL")
            SaveInt("HARD_LEVEL", a + 1)
            val time = (System.currentTimeMillis() - start) / 1000
            val sd = LoadLong("BEST_TIME_HARD")
            if (time < sd || sd == 0.toLong()) {
                /*Update best time if the previous best time was beat*/
                SaveLong("BEST_TIME_HARD", time)
            }
        } else if (LEVEL.toInt() == 2) {
            if (TIMER) {
                /*If the user has successfully completed timed, update Timed bonus appropriately*/
                SaveInt("TIMED_BONUS", timedBonusVALUE + 4)
            }
            val a = LoadInt("INSANE_LEVEL")
            SaveInt("INSANE_LEVEL", a + 1)
            val time = (System.currentTimeMillis() - start) / 1000
            val sd = LoadLong("BEST_TIME_INSANE")
            if (time < sd || sd == 0.toLong()) {
                /*Update best time if the previous best time was beat*/
                SaveLong("BEST_TIME_INSANE", time)
            }
        } else if (LEVEL.toInt() == 1) {
            if (TIMER) {
                /*If the user has successfully completed timed, update Timed bonus appropriately*/
                SaveInt("TIMED_BONUS", timedBonusVALUE + 5)
            }
            val a = LoadInt("IMPOSSIBLE_LEVEL")
            SaveInt("IMPOSSIBLE_LEVEL", a + 1)
            val time = (System.currentTimeMillis() - start) / 1000
            val sd = LoadLong("BEST_TIME_IMPOSSIBLE")
            if (time < sd || sd == 0.toLong()) {
                /*Update best time if the previous best time was beat*/
                SaveLong("BEST_TIME_IMPOSSIBLE", time)
            }
        }

        val SONGLYRICLINK = LYRICLINK
        val intent = Intent(this, CorrectSplash::class.java)
        /*Pass some parameters to the new activity for the buttons to allow the user to watch video online, lyrics and next level*/
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", YoutubeLinkOfCurrentSong)
        intent.putExtra("SONGLYRICLINK", SONGLYRICLINK)
        intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
        intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
        intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
        startActivity(intent)
    }

    fun incorrectguess(LYRICLINK: String?) {
        endTimer()// end the timer
        FindClosestMarker = false
        val LEVEL = intent.getStringExtra("Level")
        val SONGLYRICLINK = LYRICLINK
        val intent = Intent(this, IncorrectSplash::class.java)
        /* Pass some parameters to the new activity for the buttons to allow
        the user to watch video online, lyrics and same level */
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", YoutubeLinkOfCurrentSong)
        intent.putExtra("SONGLYRICLINK", SONGLYRICLINK)
        intent.putExtra("SONGLINKS", SongLinks) /* PASS ALL SONG LINKS*/
        intent.putExtra("SONGTITLES", SongTitles) /*PASS ALL SONG TITLES*/
        intent.putExtra("NUMBEROFSONGS", numberofsongs) /*PASS NUMBER OF SONGS*/
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        /* Obtain the SupportMapFragment and get notified when the map is ready to be used. */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* Get notified when the map is ready to be used. Long´running
        activities are performed asynchronously in order to keep the user
        interface responsive */
        mapFragment.getMapAsync(this)
        /* Create an instance of GoogleAPIClient. */
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        doBulkOfWork()
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    fun createLocationRequest() {
        /* Set the parameters for the location request */
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000 // preferably every 5 seconds
        mLocationRequest.fastestInterval = 1000 // at most every second
        mLocationRequest.priority =
                LocationRequest.PRIORITY_HIGH_ACCURACY
        /* Can we access the user’s current location? */
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnected(connectionHint: Bundle?) {
        try {
            createLocationRequest(); } catch (ise: IllegalStateException) {
            println("IllegalStateException thrown [onConnected]")
        }
        /* Can we access the user’s current location? */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onLocationChanged(current: Location?) {
        if (current == null) {
            Toast.makeText(this@MapsActivity, "Please enable GPS", Toast.LENGTH_LONG).show()
            println("[onLocationChanged] Location unknown")
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(current.latitude, current.longitude)))
            println("""[onLocationChanged] Lat/long now
            (${current.latitude},
            ${current.longitude})"""
            )
        }
        if (current != null && FindClosestMarker) { /* null check to ensure we don't try to distance between null and an point */
            distanceChecker(current.latitude, current.longitude)
        }
    }

    fun distanceChecker(Lat: Double, Long: Double) {
        fun distFrom(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Boolean {
            //This function gets the distance between two GPS coordinates
            val earthRadius = 6371000.0 /* meters */
            val dLat = Math.toRadians((lat2 - lat1))
            val dLng = Math.toRadians((lng2 - lng1))
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val dist = (earthRadius * c).toFloat()
            return (dist <= 15)/*return true if the distance is less than 15 */
        }
        for (i in 0..markersformap.size - 1) {
            val marker = markersformap[i]
            if (distFrom(marker.position.latitude, marker.position.longitude, Lat, Long)) {
                //println("!! THIS IS THE START")
                val markerTag = markersformap[i].tag.toString()
                val splitedTag: List<String> = markerTag.split(":").map { it.trim() } /*String into List, for getting long and lat  */
                val lineNumber = splitedTag[0].toInt()
                val positionNumber = splitedTag[1].toInt()
                try {
                    val word = words[lineNumber - 1][positionNumber - 1]
                    val markerClassification = markersformap[i].title // Gets the classification
                    vibrate()
                    Toast.makeText(this@MapsActivity, "Classification: $markerClassification  Word: $word", Toast.LENGTH_LONG).show()
                    println("!! WORD $word")
                    println("!! CLASSIFICATIOM $markerClassification")
                    println("!! CLASSIFICATIOM $markerTag")
                    markersformap[i].remove() /* remove from map*/
                    markersformap.removeAt(i) /* remove from ArrayList */
                    println("!! THIS IS THE END OF THIS WORD")
                    break

                } catch (e: IndexOutOfBoundsException) {
                    /*This catch is to ensure that if the download of the Words from the Txt file fails but the KML download
                    succeeded and the user doesn't head the advise of the pop up and try again to download or exit and
                    the user clicks on the map and they continue, they will not get a null pointer and fail.

                    This should be an exceptionally unlikely case but If it occurs, the app is ready */
                    Toast.makeText(this@MapsActivity, "Words not found, please restart game", Toast.LENGTH_LONG).show()
                    break
                }

            }
        }
    }

    override fun onConnectionSuspended(flag: Int) {
        println(" >>>> onConnectionSuspended")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        println(" >>>> onConnectionFailed")
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /* Add a marker in Sydney and move the camera */
        val sydney = LatLng(55.94438, -3.18701)
        mMap.addMarker(MarkerOptions().position(sydney).title("Appleton Tower"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setMaxZoomPreference(22.0f) /*maximum view is building level*/
        mMap.setMinZoomPreference(16.0f) /*minimum view is street level */
        val b = LoadString("MAPSTYLE")

        /*Set the MAP Style, defaults to standard if the user has not changed the setting*/
        if (b == "AUBERGINE") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style1))
        } else if (b == "RETRO") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style4))
        } else if (b == "DARK") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style3))
        } else if (b == "NIGHT") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style2))
        } else if (b == "SILVER") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style5))
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style6))
        }

        try {
            // Visualise current position with a small blue circle
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            println("Security exception thrown [onMapReady]")
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true /* Add ”My location” button to the user interface */
    }


    fun doBulkOfWork() {
        numberofsongs = intent.getIntExtra("NUMBEROFSONGS", 1) /*Number of songs in XML, please note 0 is the start */
        RandomNumberinRange = (1..numberofsongs).random() /* pick a random number */
        val LEVEL = intent.getStringExtra("Level") /* get the level the user selected */

        /* Execute KML Async */
        if (RandomNumberinRange < 10) {
            val a = "0" + RandomNumberinRange
            launchKMLDownload(a, LEVEL)
        } else {
            launchKMLDownload(RandomNumberinRange.toString(), LEVEL)
        }

        /*Obtain SongTitles and SongLinks */
        SongTitles = intent.getStringArrayExtra("SONGTITLES")
        SongLinks = intent.getStringArrayExtra("SONGLINKS")

        /*Prints for testing */
        println("%% + $RandomNumberinRange")
        println("%%" + SongTitles[RandomNumberinRange])

        /*Set up the spinner to allow users to guess the song */
        YoutubeLinkOfCurrentSong = SongLinks[RandomNumberinRange - 1] as String
        LyricLinkOfCurrentSong = RandomNumberinRange.toString()
        Spinner = findViewById<View>(R.id.spinner) as Spinner
        result = findViewById<View>(R.id.tv_result) as TextView
        Spinner.bringToFront()
        /*set an adapter with strings -where the string array is the name of the songs -  array*/

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, SongTitles)
        Spinner.adapter = adapter
        /*set click listener*/
        Spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SongTitles)
        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /*Do nothing on nothing selected*/
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = if (SongTitles[position] == SongTitles[0]) {
                vibrate()
            } else if (SongTitles[RandomNumberinRange] == SongTitles[position]) {
                /* If the user has guessed correctly */
                vibrate()
                if (RandomNumberinRange < 10) {
                    correctGuess("0" + RandomNumberinRange.toString()) /* Call function to switch activities */
                } else {
                    correctGuess(RandomNumberinRange.toString()) /* Call function to switch activities */
                }

            } else if (numberOfTriesLeft == 1) {
                /*If the user guessed incorrectly, but has no tries left boot them out of the game*/
                if (RandomNumberinRange < 10) {
                    incorrectguess("0" + RandomNumberinRange.toString()) /* Call function to switch activities */
                } else {
                    incorrectguess(RandomNumberinRange.toString()) /* Call function to switch activities */
                }
            } else {
                /* If the user guessed incorrectly, but has tries left*/
                numberOfTriesLeft -= 1
                Toast.makeText(this@MapsActivity, "Incorrect, $numberOfTriesLeft tries left", Toast.LENGTH_SHORT).show()
                vibrate()
            }
        }
    }

    fun downloadCompletDOC(result: List<List<String>>?) {
        /*executed after the DownloadDOC async task has finished */
        if (!downloadDOCFinnished) {
            /*
            This check is here to ensure that if the .txt has failed the download - e.g. Maybe due to poor network, I found
            this issue myself if I was walking down stairs and the phone was hopping between different access points, this pop up
            will appear to make the user aware that they.

            This issue particular appears with phones due to phones latching onto
            wifi connections as you walk around campus as they want to reduce 4g data used,
            even if the wifi connection is of an very
            poor quality. Thus this check should help recover from such an issue.
             */
            vibrate() /*Vibrate to notify the user*/
            alert(" Sorry \n  Downloading words failed \n Shall we retry?") {
                positiveButton("Yes please!") {
                    networkChecker()
                    if (RandomNumberinRange < 10) {
                        launchDOCdownload("0" + RandomNumberinRange.toString())
                    } else {
                        launchDOCdownload(RandomNumberinRange.toString())
                    }
                }
                negativeButton("No lets exit") {
                    networkChecker()
                    switchBackToMain()
                }
            }.show()
        } else {
            /*If the user has selected timed, the timer will be started depending on the level they selected
            if the user didn't select timed, the timer will not start in the first place. */
            val LEVEL = intent.getStringExtra("Level")
            val TIMER = intent.getBooleanExtra("Timed", false)
            if (LEVEL == "5" && TIMER) {
                /*Giving users an extra 200 to offset any delays in loading after we start the timer*/
                startTimer(1200000 + 200)
            } else if (LEVEL == "4" && TIMER) {
                startTimer(1080000 + 200)
            } else if (LEVEL == "3" && TIMER) {
                startTimer(960000 + 200)
            } else if (LEVEL == "2" && TIMER) {
                startTimer(840000 + 200)
            } else if (TIMER) {
                startTimer(720000 + 200)
            }
            start = System.currentTimeMillis()
            words = result!!
            FindClosestMarker = true
        }
    }

    fun downloadCompleteKML(result: List<EntryKml>) {
        /*This part should execute by the call back from OnPostExecute */
        if (!downloadKMLFinnished) {
            /*
            This check is here to ensure that if the KML has failed the download - e.g. Maybe due to poor network, I found
            this issue myself if I was walking down stairs and the phone was hopping between different access points, this pop up
            will appear to make the user aware that they.

            This issue particular appears with phones due to phones latching onto
            wifi connections as you walk around campus as they want to reduce 4G data used, even if the wifi connection is of an very
            poor quality. Thus this check should help recover from such an issue. */
            vibrate() /* Vibrate to notify the user that the download has failed*/
            alert(" Sorry \n  Downloading markers failed \n Shall we retry?") {
                positiveButton("Yes please!") {
                    networkChecker()
                    val LEVEL = intent.getStringExtra("Level") /* get the level the user selected */
                    if (RandomNumberinRange < 10) {
                        launchKMLDownload("0" + RandomNumberinRange.toString(), LEVEL)
                    } else {
                        launchKMLDownload(RandomNumberinRange.toString(), LEVEL)
                    }
                }
                negativeButton("No lets exit") {
                    networkChecker()
                    switchBackToMain()
                }
            }.show()
        } else {
            /* If we are getting here the KML download has completed
             thus we can now launch the DOC download,
             if this fails due to network issues, then appropriate steps will
             be taken to make the user aware and recover */
            if (RandomNumberinRange < 10) {
                launchDOCdownload("0" + RandomNumberinRange.toString())
            } else {
                launchDOCdownload(RandomNumberinRange.toString())
            }

            val numberofPoints = result.size
            NumberOfMarkers = numberofPoints
            println("%%% num of pointa" + numberofPoints)
            val PointsLong = arrayOfNulls<String>(numberofPoints + 1)
            val PointsLat = arrayOfNulls<String>(numberofPoints + 1)
            val classification = arrayOfNulls<String>(numberofPoints + 1)
            val name = arrayOfNulls<String>(numberofPoints + 1)
            /*Fill up the above arrays */
            for (i in 0..numberofPoints - 1) {
                val a = result[i].Point
                val input = a
                val result2: List<String> = input.split(",").map { it.trim() } /*String into List, for getting long and lat  */
                PointsLong[i] = result2[0]
                PointsLat[i] = result2[1]
                val theClassification = result[i].description
                classification[i] = theClassification
                val theName = result[i].name
                name[i] = theName
            }
            /*Add markers on the map according to the map with the corresponding images */
            for (i in 0..numberofPoints - 1) { /*adding the markers, with the correct icon to each depending on classification*/
                val longlat = LatLng(PointsLat[i]!!.toDouble(), PointsLong[i]!!.toDouble())
                if (classification[i] == "interesting") {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("Interesting").icon(BitmapDescriptorFactory.fromResource(R.drawable.interesting)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                } else if (classification[i] == "veryinteresting") {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("Very Interesting").icon(BitmapDescriptorFactory.fromResource(R.drawable.veryinteresting)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                } else if (classification[i] == "boring") {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("boring").icon(BitmapDescriptorFactory.fromResource(R.drawable.boring)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                } else if (classification[i] == "notboring") {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("notboring").icon(BitmapDescriptorFactory.fromResource(R.drawable.notboring)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                } else if (classification[i] == "unclassified") {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("unclassified").icon(BitmapDescriptorFactory.fromResource(R.drawable.unclassified)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                } else {
                    val marker = mMap.addMarker(MarkerOptions().position(longlat).title("unclassified").icon(BitmapDescriptorFactory.fromResource(R.drawable.unclassified)))
                    marker.tag = name[i]
                    markersformap.add(marker)
                }
            }
        }
    }
}