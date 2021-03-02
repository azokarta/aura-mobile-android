package kz.aura.merp.employee.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.ChiefActivity
import kz.aura.merp.employee.activity.DealerActivity
import kz.aura.merp.employee.activity.FinanceAgentActivity
import kz.aura.merp.employee.activity.MasterActivity
import kz.aura.merp.employee.data.model.Error
import kz.aura.merp.employee.data.model.Staff
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kz.aura.merp.employee.databinding.ErrorDialogBinding
import okhttp3.ResponseBody
import java.lang.Exception

object Helpers {

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

    fun <T> saveData(data: T, context: Context) {
        // When we change item and save, we should save to the android storage
        // Then we show the changed item in list
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        val json = Gson().toJson(data)
        editor.putString("data", json)
        editor.apply()
    }

    fun showToast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    fun definePosition(positionId: Int): StaffPosition {
        // we define position of employee and return constant
        return when (positionId) {
            4, 3, 10, 105 -> StaffPosition.DEALER
            9 -> StaffPosition.FIN_AGENT
            16, 17 -> StaffPosition.MASTER
            else -> StaffPosition.CHIEF
        }
    }

    fun getStaffId(context: Context): Long {
        val staff = getStaff(context)
        return if(staff != null) {
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
            StaffPosition.FIN_AGENT -> clearPreviousAndOpenActivity(context, FinanceAgentActivity())
            else -> clearPreviousAndOpenActivity(context, ChiefActivity())
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
                Toast.makeText(context, context.getString(R.string.unknownError), Toast.LENGTH_SHORT).show()
            }
        }
        editor.apply()
    }

    fun verifyAvailableNetwork(context: Context): Boolean {
        val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

    fun convertImageToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if(drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if(drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    fun exceptionHandler(exception: Any, context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.error_dialog, null)
        val binding = ErrorDialogBinding.bind(dialogView)
        val builder = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .show()

        when (exception) {
            is Exception -> {
                if (!verifyAvailableNetwork(context)) {
                    binding.errorTitle.text = "Интернет-соединение не подключен"
                    binding.subError.visibility = View.GONE
                } else {
                    binding.errorTitle.text = exception.message
                    binding.subError.visibility = View.GONE
                }
            }
            is ResponseBody -> {
                // Server error
                val res = Gson().fromJson(exception.charStream(), Error::class.java)
                binding.errorTitle.text = res.error
                binding.subError.text = res.message
            }
            is String -> {
                // Custom error
                binding.errorTitle.text = exception
                binding.subError.visibility = View.GONE
            }
        }

        // Hide alert dialog
        binding.errorClose.setOnClickListener {
            builder.dismiss()
        }
    }

}