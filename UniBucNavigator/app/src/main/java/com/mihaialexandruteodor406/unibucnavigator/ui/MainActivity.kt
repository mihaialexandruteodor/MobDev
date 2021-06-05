 package com.mihaialexandruteodor406.unibucnavigator.ui


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mihaialexandruteodor406.unibucnavigator.R
import com.mihaialexandruteodor406.unibucnavigator.ui.model.MarkerData
import kotlinx.coroutines.*
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MainActivity : Activity() {

     var map: MapView? = null
     var butonCalculeazaRuta: Button? = null
     var roadManager: RoadManager? = null
     var myLocation: Location? = null
     var myLocationOverlay: MyLocationNewOverlay? = null
     var locatieSelectata: Marker? = null
     var roadOverlay: Polyline? = null
     private lateinit var fusedLocationClient: FusedLocationProviderClient
     private lateinit var locationRequest: LocationRequest
     private lateinit var locationCallback: LocationCallback


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main)

        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);

        roadManager = OSRMRoadManager(this, "UniBucNavigator")

        val mapController: IMapController = map!!.getController()
        mapController.setZoom(16)

        val gson = Gson()

        var markerData = ArrayList<MarkerData>()

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Unibuc")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                markerData.clear()
                for(snapshot in dataSnapshot.child("MarkerData").children)
                {
                    var jsonString = snapshot.value.toString()
                    var mk = gson.fromJson(jsonString, MarkerData::class.java)
                    markerData.add(mk)
                }

                var latAvr = 0.0
                var lonAvr = 0.0

                for(mk in markerData)
                {
                    val marker = Marker(map)
                    marker.position = GeoPoint(mk.lat, mk.lon)
                    marker.snippet = mk.waypointName
                    marker.subDescription = mk.waypointDescript
                    marker.icon = resources.getDrawable( R.mipmap.marker, resources.newTheme())
                    Singleton.locatii.add(mk.waypointName)
                    Singleton.descriereLocatii.add(mk.waypointDescript)
                    map!!.overlays.add(marker)

                    marker.setOnMarkerClickListener { marker, mapView ->
                        locatieSelectata = marker
                        marker.showInfoWindow()
                        true
                    }

                    latAvr += mk.lat
                    lonAvr += mk.lon
                }

                latAvr /= markerData.count()
                lonAvr /= markerData.count()

                mapController.setCenter(GeoPoint(latAvr, lonAvr))
                myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)
                myLocationOverlay!!.enableMyLocation()
                map!!.overlays.add(myLocationOverlay)
                map!!.invalidate()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w( "Failed to read value.", error.toException())
            }})


    }

    public override fun onStart() {
        super.onStart()
        getLocationUpdates()
        startLocationUpdates()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.jurnal_foto -> {
                    val intent = Intent(this@MainActivity, PhotoActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Jurnal foto", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.adrese_corpuri -> {
                    val intent = Intent(this@MainActivity, LocatiiActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Adrese corpuri cladire", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        butonCalculeazaRuta = findViewById<Button?>(R.id.butonCalcRuta)
        butonCalculeazaRuta!!.setOnClickListener {
            Toast.makeText(this, "Ruta calculata cu succes!", Toast.LENGTH_SHORT).show()
            GlobalScope.launch (Dispatchers.Main) {
                ruleazaCalculRuta()
            }
        }
    }

    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        locationRequest = LocationRequest()
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty() && locationResult.lastLocation != myLocation) {

                    myLocation = locationResult.lastLocation
                    GlobalScope.launch (Dispatchers.Main) {
                        ruleazaCalculRuta()
                    }
                }


            }
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

     fun calculateRoute()
    {
        if(myLocation != null && locatieSelectata != null) {
            map!!.getOverlays().remove(roadOverlay)
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(GeoPoint(myLocation!!.latitude, myLocation!!.longitude))
            waypoints.add(locatieSelectata!!.position)
            val road: Road = roadManager!!.getRoad(waypoints)
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay!!.width = 10.0f

            map!!.getOverlays().add(roadOverlay)
            map!!.invalidate()
        }
    }

    private fun ruleazaCalculRuta() {
        var result : Int = 0
        val waitFor = CoroutineScope(Dispatchers.IO).async {
            calculateRoute()
            return@async result
        }

    }

    public override fun onResume() {
        super.onResume()
        map!!.onResume() //needed for compass, my location overlays, v6.0.0 and up
        startLocationUpdates()
    }


    public override fun onPause() {
        super.onPause()
        map!!.onPause() //needed for compass, my location overlays, v6.0.0 and up
        stopLocationUpdates()
    }

    object Singleton{
        var locatii = ArrayList<String>()
        var descriereLocatii = ArrayList<String>()

        init {
            println("Singleton class invoked.")
        }


    }

}