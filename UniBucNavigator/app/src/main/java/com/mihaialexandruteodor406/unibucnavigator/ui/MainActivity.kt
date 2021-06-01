package com.mihaialexandruteodor406.unibucnavigator.ui


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mihaialexandruteodor406.unibucnavigator.R
import com.mihaialexandruteodor406.unibucnavigator.ui.model.MarkerData
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MainActivity : Activity() {
    var map: MapView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main)
        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);

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
                    println(mk)
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
                    map!!.overlays.add(marker)

                    latAvr += mk.lat
                    lonAvr += mk.lon
                }

                latAvr /= markerData.count()
                lonAvr /= markerData.count()

                mapController.setCenter(GeoPoint(latAvr, lonAvr))

                var myLocation = GpsMyLocationProvider(ctx)
                var myLocationOverlay = MyLocationNewOverlay(myLocation, map)
                myLocationOverlay.enableMyLocation()
                map!!.overlays.add(myLocationOverlay)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w( "Failed to read value.", error.toException())
            }})


    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map!!.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    public override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map!!.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }
}