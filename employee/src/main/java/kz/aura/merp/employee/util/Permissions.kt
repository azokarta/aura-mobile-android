package kz.aura.merp.employee.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.view.PermissionsListener

class Permissions(
    owner: LifecycleOwner,
    activity: AppCompatActivity,
    private val context: Context) {

    private var listener: PermissionsListener? = null
    private var registry: ActivityResultRegistry = activity.activityResultRegistry

    fun setListener(listener: PermissionsListener) {
        this.listener = listener
    }

    private val locationPermission : ActivityResultLauncher<Array<String>> =
        registry.register("locationPermission", owner, ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.contains(false)) {
                listener?.sendResultOfRequestLocation(false)
            } else {
                listener?.sendResultOfRequestLocation(true)
            }
        }

    private val resolutionForResult : ActivityResultLauncher<IntentSenderRequest> =
        registry.register("resolutionForResult", owner, ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                listener?.sendResultOfEnableLocation(true)
            } else {
                listener?.sendResultOfEnableLocation(false)
            }
        }

    fun requestLocationPermission() {
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun locationPermissionDenied(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_DENIED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_DENIED
    }

    fun isLocationServicesEnabled(): Boolean {
        val gpsEnabled = SmartLocation.with(context).location().state().locationServicesEnabled()

        if (locationPermissionDenied()) {
            requestLocationPermission()
            return false
        }

        if (!gpsEnabled) {
            enableLocation()
            return false
        }

        return true
    }

    fun enableLocation() {
        val request = LocationRequest().setFastestInterval(5000).setInterval(1000).setPriority(
            LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
        val result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
        result.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    println("121212")
                }
            }
        }
    }

}