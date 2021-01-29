package kz.aura.merp.employee.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_map.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Location

class MapActivity : AppCompatActivity(), Session.SearchListener, CameraListener {

    private lateinit var mapView: MapView
    private var searchEdit: EditText? = null
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private lateinit var location: Location

    private fun submitQuery(query: String) {
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        setContentView(R.layout.activity_map)
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Getting a location
        SmartLocation.with(this).location()
            .start {
                println("Latitude: ${it.latitude}, longitude: ${it.longitude}")
                location = Location(it.latitude, it.longitude)
            };

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = findViewById(R.id.demo_data_mapview)

        mapView.map.addCameraListener(this)

//        searchEdit = findViewById<View>(R.id.search_edit) as EditText
        searchEdit!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit!!.text.toString())
            }
            false
        }

        mapView.map.move(
            CameraPosition(Point(location.latitude, location.longitude), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )
    }

    override fun onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // Inflate the options menu from XML
//        val inflater = menuInflater
//        inflater.inflate(R.menu.options_menu, menu)
//
//        // Get the SearchView and set the searchable configuration
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
//            // Assumes current activity is the searchable activity
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
//        }
//
//        return true
//    }


    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            println(searchResult.obj!!.name)
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(this, R.drawable.baseline_location_on_black_36dp)
                )
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = "getString(R.string.unknown_error_message)"
        if (error is RemoteError) {
            errorMessage = "getString(R.string.remote_error_message)"
        } else if (error is NetworkError) {
            errorMessage = "getString(R.string.network_error_message)"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {
        val query = searchEdit!!.text.toString()
        if (p3) {
            if (query.isNotEmpty()) {
                submitQuery(query)
            }
        }
    }

}