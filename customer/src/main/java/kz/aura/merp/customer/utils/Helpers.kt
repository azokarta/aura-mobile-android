package kz.aura.merp.customer.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import kz.aura.merp.customer.models.Error
import com.example.aura.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.error_dialog.*
import kotlinx.android.synthetic.main.error_dialog.view.*
import okhttp3.ResponseBody
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


object Helpers {

    fun loadLocale(context: Context) {
        val lang = PreferenceManager.getDefaultSharedPreferences(context).getString(
            "list_language_preference",
            ""
        ).toString()
        setLocale(lang, context)
    }
    fun decimalFormatter(number: Double?): String? {
        if (number == null) {
            return ""
        }
        // Get current locale information
        val currentLocale = Locale.getDefault()

        // Currency Formatter specific to locale
        val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(currentLocale)

        // Remove the currency symbol
        val decimalFormatSymbols: DecimalFormatSymbols =
            (currencyFormatter as DecimalFormat).getDecimalFormatSymbols()
        decimalFormatSymbols.currencySymbol = ""
        currencyFormatter.decimalFormatSymbols = decimalFormatSymbols

        return currencyFormatter.format(number)
    }

    fun dateFormatter(date: String?): String {
        if (date == null) return ""
        return if (date.isNotEmpty()) {
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd.MM.yyyy")
             formatter.format(parser.parse(date))
        } else { date }
    }

    fun setLocale(lang: String, context: Context) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            context.applicationContext.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, displayMetrics)
        }

    }

    fun getToken(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("token", "").toString()
    }

    fun setToken(context: Context, token: String) {
        val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
        preference.putString("token", token)
        preference.apply()
    }

    fun verifyAvailableNetwork(context: Context): Boolean {
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

    fun exceptionHandler(exception: Any, context: Context) {
        var builder = AlertDialog.Builder(context)
        var dialogView = LayoutInflater.from(context).inflate(R.layout.error_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        var dialog = builder.create()
        dialog.show()

        when (exception) {
            is Throwable -> {
                if (!verifyAvailableNetwork(context)) {
                    dialogView.error_title.text = "Интернет-соединение не подключен"
                    dialogView.sub_error.visibility = View.GONE
                } else {
                    dialogView.error_title.text = exception.message
                    dialogView.sub_error.visibility = View.GONE
                }
            }
            is ResponseBody -> {
                val res = Gson().fromJson(exception.charStream(), Error::class.java)
                dialog.error_title.text = res.error
                dialog.sub_error.text = res.message
            }
            is String -> {
                dialog.error_title.text = exception
                dialogView.sub_error.visibility = View.GONE
            }
            else -> Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        dialogView.error_close.setOnClickListener {
            dialog.dismiss()
        }
    }
}