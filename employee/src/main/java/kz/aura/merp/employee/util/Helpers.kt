package kz.aura.merp.employee.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.LocaleList
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ErrorDialogBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.ui.finance.activity.FinanceActivity
import kz.aura.merp.employee.util.Constants.financePositions
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.util.*
import kotlin.reflect.KClass

fun AppCompatActivity.navigateToActivity(activity: KClass<*>, newTask: Boolean = false) {
    val intent = Intent(this, activity.java).apply {
        if (newTask) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    startActivity(intent)
}

fun Fragment.navigateToActivity(activity: KClass<*>, newTask: Boolean = false) {
    val intent = Intent(requireContext(), activity.java).apply {
        if (newTask) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    startActivity(intent)
}

fun AppCompatActivity.displayToast(text: String, duration: Int) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.displayToast(text: String, duration: Int) {
    Toast.makeText(requireContext(), text, duration).show()
}

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
    // Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = activity.currentFocus
    // If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createCustomPhoneMask(mask: String): MaskImpl {
    val slots = PhoneNumberUnderscoreSlotsParser().parseSlots(mask)
    return MaskImpl.createTerminated(slots)
}

fun phoneMaskFormatWatcher(mask: String): MaskFormatWatcher {
    val slots = PhoneNumberUnderscoreSlotsParser().parseSlots(mask)
    val inputMask = MaskImpl.createTerminated(slots)
    inputMask.isForbidInputWhenFilled = true
    return MaskFormatWatcher(
        inputMask
    )
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
            else -> {
                false
            }
        }
    }
}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isConnected = false

    connectivityManager.allNetworks.forEach { network ->
        val networkCapability = connectivityManager.getNetworkCapabilities(network)
        networkCapability?.let {
            if(it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                isConnected = true
                return@forEach
            }
        }
    }

    return isConnected
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

    // Define a position of the employee and return constant
    return when (salary.positionId) {
        in financePositions -> StaffPosition.FIN_AGENT
        else -> null
    }
}

fun AppCompatActivity.openActivityByPosition(position: StaffPosition?) {
    when (position) {
        StaffPosition.FIN_AGENT -> navigateToActivity(FinanceActivity::class, true)
    }
}

fun Fragment.openActivityByPosition(position: StaffPosition?) {
    when (position) {
        StaffPosition.FIN_AGENT -> navigateToActivity(FinanceActivity::class, true)
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

    binding.errorClose.setOnClickListener {
        builder.dismiss()
    }
}
