package uk.co.iftekhar.www.songle
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.os.Vibrator;
import android.os.CountDownTimer
import org.jetbrains.anko.alert
import java.util.*
import kotlin.concurrent.timerTask

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var RandomNumberinRange = 0; //defining RandomNumberInRange to allow both AsyncTasks to access it.
    var  YoutubeLinkOfCurrentSong = ""
    var  LyricLinkOfCurrentSong = ""
    private var numberOfTriesLeft = 5
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted = false
    private var mLastLocation: Location? = null
    val TAG = "MapsActivity"
    lateinit var  Spinner: Spinner
    lateinit var result : TextView
    val timer = Timer()

    fun startTimer(minutes:Long){
        Toast.makeText(this@MapsActivity, "You have ${minutes/60000} minutes! GOOD LUCK!!", Toast.LENGTH_LONG).show()
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(500)
        timer.schedule(timerTask {
            if(RandomNumberinRange < 10){
                incorrectguess(YoutubeLinkOfCurrentSong, "0"+ LyricLinkOfCurrentSong) // Call function to switch activities
            } else {
                incorrectguess(YoutubeLinkOfCurrentSong, LyricLinkOfCurrentSong)} }, minutes)
    }
    private fun endTimer () {
        timer.cancel()      //cancel the timer
    }
    fun switchBackToMain (){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onBackPressed() {
        alert("End game?"){
            yesButton {
                endTimer()
                switchBackToMain()
            }
            noButton {
              //Don't do anything as the user has changed there mind
            }
        }.show()
    }
    fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start

    fun correctguess (YOUTUBELINK: String? , LYRICLINK : String?) {
        endTimer()
        val LEVEL = intent.getStringExtra("Level")
        val SONGLYRICLINK = LYRICLINK
        val SONGYOUTUBELINK = YOUTUBELINK
        val intent = Intent(this, CorrectSplash::class.java)
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", YoutubeLinkOfCurrentSong)
        intent.putExtra("SONGLYRICLINK", SONGLYRICLINK)
        startActivity(intent)
    }
    fun incorrectguess (YOUTUBELINK: String? , LYRICLINK : String?) {
        endTimer()// end the timer
        //If the timer runs out, or if the user guesses too many times
        val LEVEL = intent.getStringExtra("Level")
        val SONGLYRICLINK = LYRICLINK
        val SONGYOUTUBELINK = YOUTUBELINK
        val intent = Intent(this, IncorrectSplash::class.java)
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", YoutubeLinkOfCurrentSong)
        intent.putExtra("SONGLYRICLINK", SONGLYRICLINK)
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


        val XMLSONGS = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml" //link for xml file, will not change - if it does im screwed
        val XMLSongs = DownloadXmlTask(this)
        val xml = XMLSongs.execute(XMLSONGS) // Run XML async task
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
            println("IllegalStateException thrown [onConnected]") }
        /* Can we access the user’s current location? */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) } }

    override fun onLocationChanged(current: Location?) {
        if (current == null) {
            println("[onLocationChanged] Location unknown")
        } else {
            println("""[onLocationChanged] Lat/long now
            (${current.getLatitude()},
            ${current.getLongitude()})"""
            )


        }
// Do something with current location
// ... Here is where I should check for the distance between the points and the
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
    fun distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Boolean {
        val earthRadius = 6371000.0 //meters
        val dLat = Math.toRadians((lat2 - lat1).toDouble())
        val dLng = Math.toRadians((lng2 - lng1).toDouble())
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val dist = (earthRadius * c).toFloat()
        return (dist <= 10)/*return the distance */
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(55.94438, -3.18701)
        mMap.addMarker(MarkerOptions().position(sydney).title("Appleton Tower"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setMaxZoomPreference(22.0f) /*maximum view is building level*/
        mMap.setMinZoomPreference(16.0f) /*minimum view is street level */
        try {
            // Visualise current position with a small blue circle
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            println("Security exception thrown [onMapReady]")
        }
        // Add ”My location” button to the user interface
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }


    fun downloadCompleteXML(result1: List<Entry>){
        //This part should execute by the call back from OnPostExecute
        var numberofsongs = (result1.size) /*Number of songs in XML, please note 0 is the start */
        val SongTitles = arrayOfNulls<String>(numberofsongs+1)
        val SongLinks = arrayOfNulls<String>(numberofsongs+1)
        for (i in 0..numberofsongs-1) {
            val a = result1[i].link
            SongLinks[i] = a
        }
        SongTitles[0] = "GUESS THE SONG!!"
        for (i in 1..numberofsongs) {
            val b = result1[i-1].title
            SongTitles[i] = b
        }
        RandomNumberinRange = (1..numberofsongs).random()
        println("HERE + ${RandomNumberinRange}");
        println("%%" + SongTitles[RandomNumberinRange])
        YoutubeLinkOfCurrentSong = SongLinks[RandomNumberinRange-1] as String
        LyricLinkOfCurrentSong = RandomNumberinRange.toString()
        Spinner =  findViewById<View>(R.id.spinner) as Spinner
        result = findViewById<View>(R.id.tv_result) as TextView
        Spinner.bringToFront()
        /*set an adapter with strings array*/

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, SongTitles)
        Spinner.adapter = adapter
        /*set click listener*/
        Spinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,SongTitles)
        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(SongTitles[position] == SongTitles[0]) {
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                }
                else if(SongTitles[RandomNumberinRange] == SongTitles[position]){
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                    Toast.makeText(this@MapsActivity, "Correct! Well done", Toast.LENGTH_SHORT).show()
                    if(RandomNumberinRange < 10){
                        correctguess(SongLinks[RandomNumberinRange], "0"+RandomNumberinRange.toString() ) // Call function to switch activities
                    } else {
                        correctguess(SongLinks[RandomNumberinRange], RandomNumberinRange.toString() ) // Call function to switch activities
                    }
                    //do something if the user gets the correct song
                    //
                } else if (numberOfTriesLeft == 1) {
                    if(RandomNumberinRange < 10){
                        incorrectguess(SongLinks[RandomNumberinRange], "0"+RandomNumberinRange.toString() ) // Call function to switch activities
                    } else {
                        incorrectguess(SongLinks[RandomNumberinRange], RandomNumberinRange.toString() ) // Call function to switch activities
                    }
                } else {
                    numberOfTriesLeft = numberOfTriesLeft-1;
                    Toast.makeText(this@MapsActivity, "Incorrect, ${numberOfTriesLeft} tries left", Toast.LENGTH_SHORT).show()
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                }
            }
        }
        //Start the backgroud thread for downloading the KLM
        val LEVEL = intent.getStringExtra("Level")
        println("HERE2 + ${RandomNumberinRange}");
        //RandomNumberinRange = (1..numberofsongs+1).random()
        var SONGNUMBER = "";
        if(RandomNumberinRange < 10){
            val KMLMAPSURL = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0" + RandomNumberinRange +"/map"+ LEVEL +".kml"
            val KMLmap = DownloadKmlTask(this)
            KMLmap.execute(KMLMAPSURL)
        } else {
            val KMLMAPSURL = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/"+RandomNumberinRange+"/map"+ LEVEL +".kml"
            val KMLmap = DownloadKmlTask(this)
            KMLmap.execute(KMLMAPSURL)
        }

    }

    fun downloadCompleteKML(result: List<EntryKml>) {
        //This part should execute by the call back from OnPostExecute
        var numberofPoints = result.size
        println("))))" + numberofPoints)
        var PointsLong = arrayOfNulls<String>(numberofPoints+1)
        var PointsLat = arrayOfNulls<String>(numberofPoints+1)
        var classification = arrayOfNulls<String>(numberofPoints+1)
        var name = arrayOfNulls<String>(numberofPoints+1)

        for (i in 0..numberofPoints-1) {
            val a = result[i].Point
            val input = a
            var result2: List<String> = input.split(",").map { it.trim() } /*String into List  */
            PointsLong[i] = result2[0]
            PointsLat[i] = result2[1]
            val theDescription = result[i].description
            classification[i] = theDescription
            val theName = result[i].name
            name[i] = theName
        }
        println("%%%%numofpoints"+numberofPoints)
        println("%%%" + result)
        for(i in 0..numberofPoints-1){ /*adding the markers, with the correct icon to each depending on classification*/
            val longlat = LatLng(PointsLat[i]!!.toDouble(),PointsLong[i]!!.toDouble())
            if(classification[i] == "interesting"){
                mMap.addMarker(MarkerOptions().position(longlat).title("Interesting")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.interesting))
            }
            else if (classification[i] == "veryinteresting"){
                mMap.addMarker(MarkerOptions().position(longlat).title("Very Interesting")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.veryinteresting))

            }
            else if (classification[i] == "boring"){
                mMap.addMarker(MarkerOptions().position(longlat).title("boring")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boring))

            }
            else if (classification[i] == "notboring"){
                mMap.addMarker(MarkerOptions().position(longlat).title("notboring")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.notboring))

            }
            else if (classification[i] == "unclassified"){
                mMap.addMarker(MarkerOptions().position(longlat).title("unclassified")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unclassified))

            }
            else {
                mMap.addMarker(MarkerOptions().position(longlat).title("unclassified")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unclassified))
            }
        }
        val LEVEL = intent.getStringExtra("Level")
        val TIMER = intent.getBooleanExtra("Timed", false)
        if(LEVEL == "5" && TIMER ){
            startTimer(1200000)
        } else if (LEVEL == "4" && TIMER){
            startTimer(1080000)
        }else if (LEVEL == "3" && TIMER){
            startTimer(960000)
        }
        else if (LEVEL == "2" && TIMER){
            startTimer(840000 ) //15 minutes
        }
        else if (TIMER){
            startTimer(720000) //14 minutes
        }
    }

 }