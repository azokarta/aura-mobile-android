package kz.aura.merp.employee.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ErrorDialogBinding
import kz.aura.merp.employee.model.Error
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.ui.activity.AuthorizationActivity
import kz.aura.merp.employee.ui.activity.DealerActivity
import kz.aura.merp.employee.ui.activity.FinanceActivity
import kz.aura.merp.employee.ui.activity.MasterActivity
import kz.aura.merp.employee.util.Constants.SELECTED_SERVER
import kz.aura.merp.employee.util.Constants.WE_MOB_DEV_AUTH
import kz.aura.merp.employee.util.Constants.WE_MOB_DEV_CRM
import kz.aura.merp.employee.util.Constants.WE_MOB_DEV_FINANCE
import kz.aura.merp.employee.util.Constants.WE_MOB_DEV_MAIN
import kz.aura.merp.employee.util.Constants.WE_MOB_DEV_SERVICE
import kz.aura.merp.employee.util.Constants.WE_MOB_PROD_AUTH
import kz.aura.merp.employee.util.Constants.WE_MOB_PROD_CRM
import kz.aura.merp.employee.util.Constants.WE_MOB_PROD_FINANCE
import kz.aura.merp.employee.util.Constants.WE_MOB_PROD_MAIN
import kz.aura.merp.employee.util.Constants.WE_MOB_PROD_SERVICE
import kz.aura.merp.employee.util.Constants.WE_MOB_TEST_AUTH
import kz.aura.merp.employee.util.Constants.WE_MOB_TEST_CRM
import kz.aura.merp.employee.util.Constants.WE_MOB_TEST_FINANCE
import kz.aura.merp.employee.util.Constants.WE_MOB_TEST_MAIN
import kz.aura.merp.employee.util.Constants.WE_MOB_TEST_SERVICE
import kz.aura.merp.employee.util.Constants.crmPositions
import kz.aura.merp.employee.util.Constants.financePositions
import kz.aura.merp.employee.util.Constants.servicePositions
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat


fun getToken(context: Context): String? {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getString("token", "")
}

fun removeToken(context: Context) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .remove("token")
        .apply()
}

fun saveToken(context: Context, token: String) {
    PreferenceManager.getDefaultSharedPreferences(context).apply {
        edit()
            .putString("token", token)
            .apply()
    }
}

fun convertMillisToLocalDate(millis: Long): LocalDate {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy")
    return dtf.parseLocalDate(dtf.print(millis))
}

fun convertDateStrToLocalDate(date: String): LocalDate {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy")
    return dtf.parseLocalDate(date)
}

fun collectDateTimeInsideStr(dateInMillis: Long, hour: Int, minute: Int): String {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
    val date = DateTime.now().withDate(convertMillisToLocalDate(dateInMillis))
        .withHourOfDay(hour)
        .withMinuteOfHour(minute)
    return dtf.print(date)
}

fun collectDateTimeInsideStr(dateInMillis: String, hour: Int, minute: Int): String {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
    val date = DateTime.now().withDate(convertDateStrToLocalDate(dateInMillis))
        .withHourOfDay(hour)
        .withMinuteOfHour(minute)
    return dtf.print(date)
}

fun convertDateMillisToStr(millis: Long): String {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy")
    return dtf.print(millis)
}

fun convertStrToDateMillis(date: String?): Long? {
    val dtf = DateTimeFormat.forPattern("dd.MM.yyyy")
    return if (date != null) {
        dtf.parseMillis(date)
    } else null
}

fun saveStaff(context: Context, salary: Salary) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    val json = Gson().toJson(salary);
    editor.putString("staff", json)
    editor.apply()
}

fun defineUri(baseLink: Link): String =
    when (SELECTED_SERVER) {
        Server.DEV -> {
            when (baseLink) {
                Link.MAIN -> WE_MOB_DEV_MAIN
                Link.CRM -> WE_MOB_DEV_CRM
                Link.FINANCE -> WE_MOB_DEV_FINANCE
                Link.SERVICE -> WE_MOB_DEV_SERVICE
                else -> WE_MOB_DEV_AUTH
            }
        }
        Server.PROD -> {
            when (baseLink) {
                Link.MAIN -> WE_MOB_PROD_MAIN
                Link.CRM -> WE_MOB_PROD_CRM
                Link.FINANCE -> WE_MOB_PROD_FINANCE
                Link.SERVICE -> WE_MOB_PROD_SERVICE
                else -> WE_MOB_PROD_AUTH
            }
        }
        Server.TEST -> {
            when (baseLink) {
                Link.MAIN -> WE_MOB_TEST_MAIN
                Link.CRM -> WE_MOB_TEST_CRM
                Link.SERVICE -> WE_MOB_TEST_SERVICE
                Link.FINANCE -> WE_MOB_TEST_FINANCE
                else -> WE_MOB_TEST_AUTH
            }
        }
    }

fun <T> saveData(data: T, context: Context) {
    // When we change item and save, we should save to the android storage
    // Then we show the changed item in list
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    val json = Gson().toJson(data)
    editor.putString("data", json)
    editor.apply()
}

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

fun defineCorrectSalary(salaries: ArrayList<Salary>?): Salary? {
    if (salaries.isNullOrEmpty()) {
        return null
    }

    return salaries.find {
        when (it.positionId) {
            in financePositions -> true
            in servicePositions -> true
            in crmPositions -> true
            else -> {
                false
            }
        }
    }
}

fun vibrate(context: Context, millis: Long) {
    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v!!.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        //deprecated in API 26
        v!!.vibrate(millis)
    }
}

fun definePosition(salary: Salary?): StaffPosition? {
    if (salary == null) {
        return null
    }
    // Define position of employee and return constant
    return when (salary.positionId) {
        in crmPositions -> StaffPosition.DEALER
        in financePositions -> StaffPosition.FIN_AGENT
        in servicePositions -> StaffPosition.MASTER
        else -> null
    }
}

fun openActivityByPosition(context: Context, position: StaffPosition) {
    when (position) {
        StaffPosition.DEALER -> clearPreviousAndOpenActivity(context, DealerActivity())
        StaffPosition.MASTER -> clearPreviousAndOpenActivity(context, MasterActivity())
        StaffPosition.FIN_AGENT -> clearPreviousAndOpenActivity(context, FinanceActivity())
    }
}

fun clearPreviousAndOpenActivity(context: Context, activity: Activity) {
    // Clear previous activities and open new activity
    val intent = Intent(context, activity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

fun <T> saveDataByKey(context: Context, data: T, key: String) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()

    when (data) {
        is String -> editor.putString(key, data)
        is Int -> editor.putInt(key, data)
        is Long -> editor.putLong(key, data)
        is Float -> editor.putFloat(key, data)
        is Boolean -> editor.putBoolean(key, data)
        else -> {
            Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                .show()
        }
    }
    editor.apply()
}

fun verifyAvailableNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun isLocationServicesEnabled(permissions: Permissions): Boolean {
    val locationServicesEnabled = SmartLocation.with(permissions.context).location().state().locationServicesEnabled()
    val locationPermissionDenied = permissions.locationPermissionDenied()

    if (locationPermissionDenied) {
        showException(permissions.context.getString(R.string.have_not_allowed_access_to_the_location), permissions.context)
        return false
    }

    if (!locationServicesEnabled) {
        permissions.enableLocation()
        return false
    }

    return true
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun receiveErrorMessage(errorBody: ResponseBody): String? {
    val res = Gson().fromJson(errorBody.charStream(), Error::class.java)
    return res.message
}

fun declareErrorByStatus(message: String?, status: Int? = null, context: Context, activity: Activity? = null) {
    val hasInternetConnection = verifyAvailableNetwork(context)
    if (!hasInternetConnection) {
        showException(context.getString(R.string.network_disconnected), context)
        return
    }
    when (status) {
        401 -> {
            if (activity != null && activity is AuthorizationActivity) {
                showToast(context, context.getString(R.string.wrong_login_or_password))
            } else {
                showToast(context, context.getString(R.string.wrong_login_or_password))
                clearPreviousAndOpenActivity(context, AuthorizationActivity())
            }
        }
        400 -> showException(message, context, context.getString(R.string.bad_request))
        500 -> showException(message, context, context.getString(R.string.internal_server_error))
        404 -> showException(message, context, context.getString(R.string.not_found))
        else -> showException(message, context)
    }
}

fun showException(message: String? = null, context: Context, title: String? = null) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.error_dialog, null)
    val binding = ErrorDialogBinding.bind(dialogView)
    val builder = MaterialAlertDialogBuilder(context)
        .setView(dialogView)
        .show()

    binding.errorTitle.text = title
    binding.subError.text = message

    // Hide alert dialog
    binding.errorClose.setOnClickListener {
        builder.dismiss()
    }
}
