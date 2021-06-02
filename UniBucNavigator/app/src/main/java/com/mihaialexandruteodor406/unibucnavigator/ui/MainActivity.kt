package com.mihaialexandruteodor406.unibucnavigator.ui


import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mihaialexandruteodor406.unibucnavigator.R
import com.mihaialexandruteodor406.unibucnavigator.ui.model.MarkerData
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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
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
                myLocation.addLocationSource(LocationManager.NETWORK_PROVIDER)
                var myLocationOverlay = MyLocationNewOverlay(myLocation, map)
                myLocationOverlay.enableMyLocation()
                map!!.overlays.add(myLocationOverlay)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w( "Failed to read value.", error.toException())
            }})


    }

    public override fun onStart() {
        super.onStart()
        butonCalculeazaRuta = findViewById<Button?>(R.id.butonCalcRuta)
        butonCalculeazaRuta!!.setOnClickListener( View.OnClickListener() {
            //Toast.makeText(applicationContext, "Se calculeaza ruta...", Toast.LENGTH_SHORT).show()
            val waypoints = ArrayList<GeoPoint>()
            val startPoint = GeoPoint(44.469791374161645, 25.981917745235574)
            waypoints.add(startPoint)
            val endPoint = GeoPoint(44.396312322383544, 26.209673223378744)
            waypoints.add(endPoint)
            val thread = Thread(Runnable {
                try {
                    val road: Road = roadManager!!.getRoad(waypoints)
                    val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road)
                    map!!.getOverlays().add(roadOverlay)
                    map!!.invalidate()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()

        })
    }


    public override fun onResume() {
        super.onResume()
        map!!.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }


    public override fun onPause() {
        super.onPause()
        map!!.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }
}