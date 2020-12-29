package kz.aura.merp.customer.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.aura.R
import kotlinx.android.synthetic.main.activity_ocr_web.*
import kz.aura.merp.customer.presenters.AuthPresenter
import kz.aura.merp.customer.presenters.IAuthPresenter
import kz.aura.merp.customer.presenters.ICustomerPresenter
import kz.aura.merp.customer.util.Constants.OCR_URL
import kz.aura.merp.customer.util.ProgressDialog
import kz.aura.merp.customer.views.IAuthorizationView

class OcrWebActivity : AppCompatActivity(), IAuthorizationView {

    private lateinit var authPresenter: IAuthPresenter
    private lateinit var progressDialog: ProgressDialog
    private var transactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_web)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = "OCR"

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        authPresenter = AuthPresenter(this, this)

        authPresenter.fetchTracsactionId()
    }

    private fun initOcrWebView(transactionId: String) {
        // Initialize OCR web view
        ocr_web.settings.javaScriptEnabled = true
        ocr_web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                ocr_progress_bar.visibility = View.INVISIBLE
            }
        }
        ocr_web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }

        ocr_web.loadUrl(OCR_URL + transactionId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.auth_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_close -> {
                progressDialog.showLoading()
                transactionId?.let { authPresenter.fetchToken(it) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onSuccessTransactionId(transactionId: String) {
        this.transactionId = transactionId
        initOcrWebView(transactionId)
    }

    override fun onSuccessAuth() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}