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
import java.util.ArrayList


class MainActivity : Activity() {
    var map: MapView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main)
        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
        map!!.setBuiltInZoomControls(true);
        map!!.setMultiTouchControls(true);

        val mapController: IMapController = map!!.getController()
        mapController.setZoom(15)

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
                    map!!.getOverlays().add(marker)

                    latAvr += mk.lat
                    lonAvr += mk.lon
                }

                latAvr /= markerData.count()
                lonAvr /= markerData.count()

                mapController.setCenter(GeoPoint(latAvr, lonAvr))
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w( "Failed to read value.", error.toException())
            }})




        /*val mk = MarkerData("Universitatea din Bucuresti",  "Universitatea din București este o universitate de stat din București și una dintre cele mai prestigioase instituții de învățământ superior din România. Fondată în 1864, Universitatea din București este a doua universitate modernă a României, după Universitatea din Iași.",
                44.43553081023589, 26.101741700286187)

        FirebaseDatabase.getInstance().getReference().child("Unibuc")
                .child("MarkerData").push().setValue(gson.toJson(mk))*/
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