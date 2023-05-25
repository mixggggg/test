package com.example.test_projekt.fragments


import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.test_projekt.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView


class MapFragment : Fragment() {

    private lateinit var  mMapView: MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

   // private val mMapController: MapController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
       // Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mMapView = MapView(requireContext())

        mMapView.findViewById<MapView>(R.id.map)
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
       // mMapView.setBuiltInZoomControls(true)
        mMapView.setMultiTouchControls(true)


        val mMapController = mMapView.controller
        mMapController.setZoom(9.5)
        val startPoint = GeoPoint (55.442400, 37.363600)
        mMapController.setCenter(startPoint)
    }

    override fun onResume() {
        super.onResume()
        var prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().save(requireContext(),prefs)
        mMapView.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}