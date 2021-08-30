package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kz.aura.merp.employee.base.AppPreferences
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.model.Message
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    val preferences: AppPreferences
) : ViewModel() {

    val messagesResponse = MutableLiveData<NetworkResult<List<Message>>>()

    fun fetchMessages(staffId: Long?) {
        val db = Firebase.firestore
        db.collection("users").document(staffId.toString()).collection("messages")
            .addSnapshotListener { documents, e ->
                if (e != null) {
                    Timber.tag("Firestore").d("Listen failed.")
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val messages = ArrayList<Message>()
                    for (doc in documents) {
                        val message = Message(
                            doc.getLong("fromId"),
                            doc.getString("createdAt"),
                            doc.getString("from"),
                            doc.getString("message")
                        )
                        messages.add(message)
                    }

                    // Sort by date
                    val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
                    val sortedMessages = messages.sortedBy { dtf.parseLocalDate(it.createdAt) }

                    messagesResponse.postValue(NetworkResult.Success(sortedMessages))
                }
            }
    }
}