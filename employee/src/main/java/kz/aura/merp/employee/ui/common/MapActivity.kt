package kz.aura.merp.employee.ui.common

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.mapkit.transport.masstransit.SectionMetadata.SectionData
import com.yandex.mapkit.transport.masstransit.Session
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.model.Location
import kz.aura.merp.employee.databinding.ActivityMapBinding

class MapActivity : BaseActivity(), OnLocationUpdatedListener, Session.RouteListener, com.yandex.mapkit.search.Session.SearchListener {

    private lateinit var mapView: MapView
    private lateinit var location: Location
    private lateinit var binding: ActivityMapBinding

    private var routeStartLocation: Point? = null
    private lateinit var routeEndLocation: Point

    private lateinit var mapObjects: MapObjectCollection
    private var mtRouter: MasstransitRouter? = null

    private var searchManager: SearchManager? = null
    private var searchSession: com.yandex.mapkit.search.Session? = null

    private fun submitQuery(point: Point) {
        if (searchManager != null) {
            searchSession = searchManager!!.submit(
                point,
                16,
                SearchOptions(),
                this
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        TransportFactory.initialize(this)
        SearchFactory.initialize(this)
        super.onCreate(savedInstanceState)

        val receivedLocation = intent.getParcelableExtra<Location>("location")!!
        routeEndLocation = Point(receivedLocation.latitude, receivedLocation.longitude)

        // Data binding
        binding = ActivityMapBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.pointB = receivedLocation.address
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.map)

        // Getting a location
        SmartLocation.with(this).location().oneFix().start(this)

        mapView = findViewById(R.id.demo_data_mapview)

        mapObjects = mapView.map.mapObjects.addCollection()

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

    }

    private fun moveCameraToCurrentLocation() {
        mapView.map.move(
            CameraPosition(Point(location.latitude, location.longitude), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2F),
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

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }

    override fun onLocationUpdated(p0: android.location.Location?) {
        if (p0 != null) {
            println("Latitude: ${p0.latitude}, longitude: ${p0.longitude}")
            binding.pointA = "Мамыр"
            location = Location(p0.latitude, p0.longitude)
            moveCameraToCurrentLocation()
            routeStartLocation = Point(p0.latitude, p0.longitude)
            submitQuery(Point(p0.latitude, p0.longitude))

            val options = MasstransitOptions(ArrayList(), ArrayList(), TimeOptions())
            val points: MutableList<RequestPoint> = ArrayList()
            points.add(RequestPoint(routeStartLocation!!, RequestPointType.WAYPOINT, null))
            points.add(RequestPoint(routeEndLocation, RequestPointType.WAYPOINT, null))
            mtRouter = TransportFactory.getInstance().createMasstransitRouter()
            mtRouter!!.requestRoutes(points, options, this)
        }
    }


    override fun onMasstransitRoutes(routes: List<Route>) {
        // In this example we consider first alternative only
        if (routes.isNotEmpty()) {
            for (section in routes[0].sections) {
                drawSection(
                    section.metadata.data,
                    SubpolylineHelper.subpolyline(
                        routes[0].geometry, section.geometry
                    )
                )
            }
        }
    }

    private fun showUnknownError() = Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show()

    override fun onMasstransitRoutesError(p0: com.yandex.runtime.Error) = showUnknownError()

    private fun drawSection(
        data: SectionData,
        geometry: Polyline
    ) {
        // Draw a section polyline on a map
        // Set its color depending on the information which the section contains
        // Draw a section polyline on a map
        // Set its color depending on the information which the section contains
        val polylineMapObject = mapObjects.addPolyline(geometry)
        // Masstransit route section defines exactly one on the following
        // 1. Wait until public transport unit arrives
        // 2. Walk
        // 3. Transfer to a nearby stop (typically transfer to a connected
        //    underground station)
        // 4. Ride on a public transport
        // Check the corresponding object for null to get to know which
        // kind of section it is
        // Masstransit route section defines exactly one on the following
        // 1. Wait until public transport unit arrives
        // 2. Walk
        // 3. Transfer to a nearby stop (typically transfer to a connected
        //    underground station)
        // 4. Ride on a public transport
        // Check the corresponding object for null to get to know which
        // kind of section it is
        if (data.transports != null) {
            // A ride on a public transport section contains information about
            // all known public transport lines which can be used to travel from
            // the start of the section to the end of the section without transfers
            // along a similar geometry
            for (transport in data.transports!!) {
                // Some public transport lines may have a color associated with them
                // Typically this is the case of underground lines
                if (transport.line.style != null) {
                    polylineMapObject.strokeColor = transport.line.style!!.color!! or -0x1000000
                    return
                }
            }
            // Let us draw bus lines in green and tramway lines in red
            // Draw any other public transport lines in blue
            val knownVehicleTypes: HashSet<String> = HashSet()
            knownVehicleTypes.add("bus")
            knownVehicleTypes.add("tramway")
            for (transport in data.transports!!) {
                val sectionVehicleType = getVehicleType(transport, knownVehicleTypes)
                if (sectionVehicleType == "bus") {
                    polylineMapObject.strokeColor = -0xff0100 // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    polylineMapObject.strokeColor = -0x10000 // Red
                    return
                }
            }
            polylineMapObject.strokeColor = -0xffff01 // Blue
        } else {
            // This is not a public transport ride section
            // In this example let us draw it in black
            polylineMapObject.strokeColor = -0x1000000 // Black
        }
    }

    private fun getVehicleType(transport: Transport, knownVehicleTypes: HashSet<String>): String? {
        // A public transport line may have a few 'vehicle types' associated with it
        // These vehicle types are sorted from more specific (say, 'histroic_tram')
        // to more common (say, 'tramway').
        // Your application does not know the list of all vehicle types that occur in the data
        // (because this list is expanding over time), therefore to get the vehicle type of
        // a public line you should iterate from the more specific ones to more common ones
        // until you get a vehicle type which you can process
        // Some examples of vehicle types:
        // "bus", "minibus", "trolleybus", "tramway", "underground", "railway"
        for (type in transport.line.vehicleTypes) {
            if (knownVehicleTypes.contains(type)) {
                return type
            }
        }
        return null
    }

    override fun onSearchResponse(response: Response) {
        val resultLocation = response.collection.children[0].obj!!
        binding.pointA = resultLocation.name
    }

    override fun onSearchError(p0: com.yandex.runtime.Error) = showUnknownError()
}