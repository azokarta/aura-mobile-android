package kz.aura.merp.customer.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.aura.R

class ProgressDialog(context: Context) {

    private val dialogView: View = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
    private val builder = AlertDialog.Builder(context)
    private var dialog: AlertDialog

    init {
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showLoading() {
        dialog.show()
    }

    fun hideLoading() {
        dialog.dismiss()
    }

}