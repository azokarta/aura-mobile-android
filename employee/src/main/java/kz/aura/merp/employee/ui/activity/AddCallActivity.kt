package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityAddCallBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class AddCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCallBinding

    private lateinit var progressDialog: ProgressDialog
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var callDirectionId: Long = 1L
    private var startedTime: DateTime? = null
    private var duration: Duration? = null
    private var selectedStatusId: Long = 0L
    private var contractId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        callDirectionId = intent.getLongExtra("callDirectionId", 1)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObserver()

        mFinanceViewModel.fetchCallStatuses()

        startedTime = DateTime.now()

        binding.save.setOnClickListener(::save)

        binding.callStatusText.setOnItemClickListener { _, _, i, _ ->
            selectedStatusId = mFinanceViewModel.callStatusesResponse.value!!.data!![i].id
        }

        dialPhoneNumber(phoneNumber)
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivityForResult(intent, 1000)
    }

    private fun save(view: View) {
        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val formattedDate: String = dtf.print(startedTime)
        if (selectedStatusId != 0L) {
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        phoneNumber,
                        selectedStatusId,
                        callDirectionId,
                        formattedDate,
                        duration!!,
                        description,
                        it.longitude,
                        it.latitude
                    )
                    println(assign)
                    mFinanceViewModel.assignCall(assign, contractId)
                }
        }
    }

    private fun setupObserver() {
        mFinanceViewModel.callStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    if (!res.data.isNullOrEmpty()) {
                        val adapter = ArrayAdapter(
                            this,
                            R.layout.list_item,
                            res.data.map { it.name })
                        (binding.callStatusField.editText as? AutoCompleteTextView)?.setAdapter(
                            adapter
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.assignCallResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    intent.putExtra("calls", res.data)
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val endTime = DateTime.now()
            duration = Duration(startedTime, endTime)
        }
    }
}