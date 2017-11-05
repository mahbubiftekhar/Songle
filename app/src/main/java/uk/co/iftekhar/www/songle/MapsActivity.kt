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
        vibratorService.vibrate(600)
        timer.schedule(timerTask { incorrectguess() }, minutes)
    }
    private fun endTimer () {
        timer.cancel()
      //cancel the timer
    }
    override fun onBackPressed() {
        alert("End game?"){
            yesButton {
                endTimer()
                super.onBackPressed()
            }
            noButton {
              //Don't do anything as the user has changed there mind
            }
        }.show()

    }

    fun correctguess () {
        endTimer()
        val LEVEL = intent.getStringExtra("CURRENTLEVEL")
        val SONGYOUTUBELINK = intent.getStringExtra("SONGLINKYOUTUBE")
        val SONGLYRICLINK = intent.getStringExtra("SONGLINKLYRIC")
        val intent = Intent(this, CorrectSplash::class.java)
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", SONGYOUTUBELINK)
        intent.putExtra("SONGLYRICLINK", SONGLYRICLINK)
        startActivity(intent)
    }
    fun incorrectguess () {
        endTimer()// end the timer
        //If the timer runs out, or if the user guesses too many times
        val LEVEL = intent.getStringExtra("CURRENTLEVEL")
        val SONGYOUTUBELINK = intent.getStringExtra("SONGLINKYOUTUBE")
        val SONGLYRICLINK = intent.getStringExtra("SONGLINKLYRIC")
        val intent = Intent(this, IncorrectSplash::class.java)
        intent.putExtra("LEVEL", LEVEL)
        intent.putExtra("SONGYOUTUBELINK", SONGYOUTUBELINK)
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
        val THESONGNAME = intent.getStringExtra("THESONGNAME")
        val THESONGLINK = intent.getStringExtra("THESONGLINK")

        /*Find the id of spinner*/
        Spinner =  findViewById<View>(R.id.spinner) as Spinner
        result = findViewById<View>(R.id.tv_result) as TextView
        Spinner.bringToFront()
        /*set an adapter with strings array*/
        val options = intent.getStringArrayExtra("SongTitles")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, options)
        Spinner.adapter = adapter

        /*set click listener*/
        Spinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)
        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(options[position] == options[0]) {
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                }
                else if(THESONGNAME == options[position]){
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                    Toast.makeText(this@MapsActivity, "Correct! Well done", Toast.LENGTH_SHORT).show()
                    correctguess() // Call function to switch activities
                    //do something if the user gets the correct song
                    //
                    } else if (numberOfTriesLeft == 1) {
                    incorrectguess()
                } else {
                    numberOfTriesLeft = numberOfTriesLeft-1;
                    Toast.makeText(this@MapsActivity, "Incorrect, ${numberOfTriesLeft} tries left", Toast.LENGTH_SHORT).show()
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                }
            }
        }
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

        val PointsLong = intent.getStringArrayExtra("PointsLong") /*receives PointsLong data from ActivityMain*/
        val PointsLat = intent.getStringArrayExtra("PointsLat") /*receives PointsLang data from ActivityMain*/
        val classification = intent.getStringArrayExtra("classification")
        val name = intent.getStringArrayExtra("name")
        val numberofPoints = intent.getIntExtra("numberofmarkers",0)

        for(i in 0..numberofPoints){ /*adding the markers, with the correct icon to each depending on classification*/
            val longlat = LatLng(PointsLat[i].toDouble(),PointsLong[i].toDouble())
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
        val LEVEL = intent.getStringExtra("CURRENTLEVEL")
        val TIMER = intent.getBooleanExtra("Timed", false)
        println("$$$ we are getting ${TIMER}")
        if(LEVEL == "5" && TIMER ){
            startTimer(1200000)
            println("%%GOTHERE5")
        } else if (LEVEL == "4" && TIMER){
            println("%%GOTHERE4")
            startTimer(1080000)
        }else if (LEVEL == "3" && TIMER){
            println("%%GOTHERE3")
            startTimer(960000)
        }
        else if (LEVEL == "2" && TIMER){
            println("%%GOTHERE2")
            startTimer(840000 ) //15 minutes
        }
        else if (TIMER){
            println("%%GOTHERE1")
            startTimer(720000) //14 minutes
        }

        //otherwise it is not timered
    }


    }