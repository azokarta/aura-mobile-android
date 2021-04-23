package kz.aura.merp.employee.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.preference.PreferenceManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.Error
import kz.aura.merp.employee.model.Staff
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kz.aura.merp.employee.databinding.ErrorDialogBinding
import kz.aura.merp.employee.ui.activity.*
import kz.aura.merp.employee.ui.activity.AuthorizationActivity
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
import okhttp3.ResponseBody

fun getToken(context: Context): String? {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getString("token", "")
}

fun saveStaff(context: Context, staff: Staff) {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = pref.edit()
    val json = Gson().toJson(staff);
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

fun showToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

fun definePosition(positionId: Int): StaffPosition? {
    // Define position of employee and return constant
    return when (positionId) {
        4, 3, 10, 105 -> StaffPosition.DEALER
        9 -> StaffPosition.FIN_AGENT
        16, 17 -> StaffPosition.MASTER
        else -> null
    }
}

fun getStaffId(context: Context): Long {
    val staff = getStaff(context)
    return if (staff != null) {
        // Employees use staff
        staff.staffId
    } else {
        // Chiefs use staff id
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.getLong("staffId", 0L)
    }
}

fun getStaff(context: Context): Staff? {
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val data = pref.getString("staff", "")
    return if (!data.isNullOrEmpty()) {
        val obj: Staff = Gson().fromJson<Staff>(data, Staff::class.java)
        obj
    } else {
        null
    }
}

fun getPositionId(context: Context): Int? {
    val staff = getStaff(context)
    return staff?.let { it.salaryDtoList[0].positionId }
}

fun openActivityByPositionId(context: Context) {
    val positionId = getPositionId(context)
    when (positionId?.let { definePosition(it) }) {
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
            Toast.makeText(context, context.getString(R.string.unknownError), Toast.LENGTH_SHORT)
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

fun receiveErrorMessage(errorBody: ResponseBody): String? {
    val res = Gson().fromJson(errorBody.charStream(), Error::class.java)
    return res.message
}

fun declareErrorByStatus(message: String?, status: Int? = null, context: Context, activity: Activity? = null) {
    val hasInternetConnection = verifyAvailableNetwork(context)
    if (!hasInternetConnection) {
        showException(context.getString(R.string.networkDisconnected), context)
        return
    }
    when (status) {
        401 -> {
            if (activity != null && activity is AuthorizationActivity) {
                showToast(context, context.getString(R.string.wrongLoginOrPassword))
            } else {
                showToast(context, context.getString(R.string.wrongLoginOrPassword))
                clearPreviousAndOpenActivity(context, AuthorizationActivity())
            }
        }
        400 -> showException(message, context, "Bad request")
        500 -> showException(message, context, "Internal server error")
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

fun getReportsListFinance() : String {
    return "<!doctype html>\n" +
            "<html lang=\"en\">\n" +
            "  <head>\n" +
            "    <!-- Required meta tags -->\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "\n" +
            "    <title>Hello, world!</title>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <h1>Hello, world!</h1>\n" +
            """   for(i in 1..20) 'a' """ +
            "  </body>\n" +
            "</html>"
}