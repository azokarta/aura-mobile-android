package kz.aura.merp.employee.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_ocr_web.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.viewmodel.AuthViewModel
import kz.aura.merp.employee.util.Constants
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.StaffPosition


class OcrWebFragment : Fragment() {

    private val args: OcrWebFragmentArgs by navArgs()

    private val mAuthViewModel: AuthViewModel by viewModels()
    private var transactionId: String? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_ocr_web, container, false)

        // Get transactionId
        transactionId = args.transactionId

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        // Initialize OCR web view
        mView.ocr_web.settings.javaScriptEnabled = true
        mView.ocr_web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                mView.ocr_progress_bar.visibility = View.INVISIBLE
            }
        }
        mView.ocr_web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }


        mView.ocr_web.loadUrl(Constants.OCR_URL + transactionId)

//        test.settings.javaScriptEnabled = true
//        test.addJavascriptInterface(object : Any() {
//            @JavascriptInterface // For API 17+
//            fun close() {
//                println("DWDWDWD")
//            }
//        }, "ocr")
//
//
//        test.loadUrl("file:///android_asset/index.html")

        // Observe MutableLiveData
        mAuthViewModel.positionId.observe(viewLifecycleOwner, Observer { data ->
            progressDialog.hideLoading() // hide loading
            val positionId = Helpers.getPositionId(requireContext())
            when (Helpers.definePosition(positionId!!)) {
//                StaffPosition.DEALER -> openFragment(R.id.dealerFragment)
//                StaffPosition.MASTER -> openFragment(R.id.masterFragment)
//                StaffPosition.FIN_AGENT -> openFragment(R.id.financeFragment)
//                else -> openFragment(R.id.chiefFragment)
            }
        })
        mAuthViewModel.onErrorToken.observe(viewLifecycleOwner, Observer {
            progressDialog.hideLoading() // hide loading
//            findNavController().navigate(R.id.action_authorizationFragment)
        })
        return mView
    }

    private fun openFragment(id: Int) = findNavController().navigate(id)

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.auth_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_close -> {
                progressDialog.showLoading()
                mAuthViewModel.fetchToken(transactionId!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}