package com.example.test_projekt

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test_projekt.databinding.ActivityMainBinding
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var  mMapView: MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var mMarker: Marker
    private lateinit var marker: Marker
    private lateinit var road: Road
    private lateinit var roadManager: OSRMRoadManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(binding.root)

        permitionListener()
        requestPermissionsIfNecessary()
        loadMap()

        roadManager = OSRMRoadManager(this, String())

        val radioButtonCentr = binding.buttonCentr
        val radioButtonFokus = binding.buttonFokus
        val radioButtonExit = binding.buttonExit

        radioButtonExit.setOnClickListener {

            mMapView.overlays.clear()
            mMapView.controller.animateTo(1, 1)
        }

        radioButtonCentr.setOnClickListener {

            if (mMarker != null) {
                mMarker.isDraggable = true
            }
            mMapView.overlays.add(mMarker)
            mMapView.overlays.add(marker)
            mMarker.position = mMapView.mapCenter as GeoPoint
            mMarker.isDraggable = true
            mMapView.controller.animateTo(1, 1)
        }

        radioButtonFokus.setOnClickListener {

            if (mMarker != null){
                mMarker.isDraggable = false
            }

            val thread = Thread(Runnable()
            {
                fun run()
                {
                    roadManager = OSRMRoadManager(this, String());
                    val waypoints = ArrayList<GeoPoint>()
                    val startPoint = GeoPoint(mMarker.position);
                    waypoints.add(startPoint);
                    val endPoint = GeoPoint(marker.position);
                    waypoints.add(endPoint);
                    try
                    {
                        road = roadManager.getRoad(waypoints);
                    }
                    catch (e:Exception )
                    {
                        e.printStackTrace();
                    }

                    runOnUiThread(Runnable()
                    {
                        fun run()
                        {
                            if (road.mStatus != Road.STATUS_OK)
                            {
                                //handle error... warn the user, etc.
                            }

                            var roadOverlay = RoadManager.buildRoadOverlay(road, Color.RED, 8F, );
                            mMapView.overlays.add(roadOverlay)
                            mMapView.controller.animateTo(1,1)
                        }
                        return@Runnable run()
                    })
                }
                return@Runnable run()
            })
            thread.start()
            mMapView.invalidate()

        }
    }

    fun loadMap(){

        mMapView = MapView(this)
        mMapView = binding.map
        mMapView.setTileSource(TileSourceFactory.MAPNIK)
        Configuration.getInstance().userAgentValue
        mMapView.setBuiltInZoomControls(true)
        mMapView.setMultiTouchControls(true)

        val mPoint = GeoPoint(55.740172, 37.620329)
        mMarker = Marker(mMapView)
        mMarker.position = mPoint
        mMarker.title = "Pizza Sushi Wok"
        mMarker.icon = resources.getDrawable(R.mipmap.ic_launcher_round)
        mMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mMapView.overlays.add(mMarker)
        mMapView.invalidate()

        val point = GeoPoint(55.764661, 37.645049)
        marker = Marker(mMapView)
        marker.position = point
        marker.title = "Жду заказ"
        marker.icon = resources.getDrawable(R.drawable.icon_pizza)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.isDraggable = true
        mMapView.overlays.add(marker)
        mMapView.invalidate()

        val mapController = mMapView.controller
        mapController.setZoom(14)
        val startPoint = mPoint
        mapController.setCenter(startPoint)

        val rotationGestureOverlay = RotationGestureOverlay(mMapView)
        rotationGestureOverlay.isEnabled
        mMapView.setMultiTouchControls(true)
        mMapView.overlays.add(rotationGestureOverlay)

        }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun requestPermissionsIfNecessary(){
        when{
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ->{

                    }

            else ->{
                pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun permitionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }
}