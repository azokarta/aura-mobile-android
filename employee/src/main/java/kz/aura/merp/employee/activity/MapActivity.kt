package kz.aura.merp.employee.activity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Constants
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity(), Session.SearchListener, CameraListener {

    private lateinit var mapView: MapView
    private var searchEdit: EditText? = null
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private fun submitQuery(query: String) {
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        MapKitFactory.setApiKey(Constants.YANDEX_MAP_API_KEY)
        MapKitFactory.initialize(applicationContext)
        SearchFactory.initialize(this)

        setContentView(R.layout.activity_map)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = "Карта"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = findViewById(R.id.demo_data_mapview)

        mapView.map.addCameraListener(this)
        searchEdit = findViewById<View>(R.id.search_edit) as EditText
        searchEdit!!.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit!!.text.toString())
            }
            false
        }
        mapView.map.move(
            CameraPosition(Point(59.945933, 30.320045), 14.0f, 0.0f, 0.0f)
        )
        submitQuery(searchEdit!!.text.toString())
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(this, R.drawable.circle)
                )
            }
        }
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            submitQuery(searchEdit!!.text.toString())
        }
    }

}