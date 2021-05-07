package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityOutgoingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@AndroidEntryPoint
class OutgoingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutgoingBinding

    private lateinit var progressDialog: ProgressDialog
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var callDirectionId: Long = 1L
    private var contractId: Long = 0L
    private var callStatusId: Long = 0L
    private var duration: Duration? = null
    private var startedTime: DateTime? = null
    private val requestCode = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgoingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_outgoing_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        startedTime = DateTime.now()

        binding.save.setOnClickListener(::save)

        binding.callStatusText.setOnItemClickListener { _, _, i, _ ->
            callStatusId = mFinanceViewModel.callStatusesResponse.value!!.data!![i].id
        }

        binding.phoneNumberText.setText(phoneNumber)

        setupObservers()

        mFinanceViewModel.fetchCallStatuses()

        dialPhoneNumber(phoneNumber)
    }

    private fun setupObservers() {
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


    private fun save(view: View) {
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val currentDate: String = dtf.print(DateTime.now())

        if (phoneNumber.isNotBlank() && callStatusId != 0L) {
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        typedPhoneNumber,
                        null,
                        callDirectionId,
                        currentDate,
                        duration!!.toString(),
                        description,
                        it.longitude,
                        it.latitude
                    )
                    mFinanceViewModel.assignCall(assign, contractId)
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            val endTime = DateTime.now()
            duration = Duration(startedTime, endTime)
        }
    }
}