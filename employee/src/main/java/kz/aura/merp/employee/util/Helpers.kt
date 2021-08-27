package kz.aura.merp.employee.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.os.LocaleList
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ErrorDialogBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.ui.finance.activity.FinanceActivity
import kz.aura.merp.employee.util.Constants.crmPositions
import kz.aura.merp.employee.util.Constants.financePositions
import kz.aura.merp.employee.util.Constants.servicePositions
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*

fun updateLocale(c: Context, language: String): ContextWrapper {
    var context = c
    val localeToSwitchTo = Locale(language)
    val resources: Resources = context.resources
    val configuration: Configuration = resources.configuration
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val localeList = LocaleList(localeToSwitchTo)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
    } else {
        configuration.locale = localeToSwitchTo
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        context = context.createConfigurationContext(configuration)
    } else {
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    return ContextWrapper(context)
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

fun defineCorrectSalary(salaries: List<Salary>?): Salary? {
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
        StaffPosition.FIN_AGENT -> newTask(context, FinanceActivity::class.java)
    }
}

fun newTask(context: Context, activity: Class<*>) {
    // Clear previous activities and open new activity
    val intent = Intent(context, activity)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
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
