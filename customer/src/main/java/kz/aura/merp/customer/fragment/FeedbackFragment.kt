package kz.aura.merp.customer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aura.R
import kz.aura.merp.customer.activity.MainActivity
import kz.aura.merp.customer.adapter.FeedbackAdapter
import kz.aura.merp.customer.data.model.Feedback
import kz.aura.merp.customer.presenters.FeedbackPresenter
import kz.aura.merp.customer.presenters.IFeedbackPresenter
import kz.aura.merp.customer.util.Constants
import kz.aura.merp.customer.views.IFeedbackView
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment(), IFeedbackView {

    private lateinit var feedbackPresenter: IFeedbackPresenter
    private val feedbackList = arrayListOf<Feedback>()
    private var filterComments = arrayListOf<Feedback>()
    private val feedbackAdapter = FeedbackAdapter(feedbackList, filterComments)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "Отзывы"

        feedback_recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        feedback_recyclerView.adapter = feedbackAdapter

        hideProgressbar(false)
        feedbackPresenter = FeedbackPresenter(this, this.requireContext())
        feedbackPresenter.getFeedback(Constants.CUSTOMER_ID)
    }

    override fun onSuccess(feedback: ArrayList<Feedback>) {
        hideProgressbar(true)
        feedbackList.addAll(feedback)
        filterComments.addAll(feedback.filter { it.parentId == 0.toLong() || it.parentId == null } as ArrayList<Feedback>)
        feedbackAdapter.notifyDataSetChanged()
    }

    override fun onError(error: Any) {
        hideProgressbar(true)
    }

    private fun hideProgressbar(visibility: Boolean) {
        if (visibility) {
            feedback_progress_bar.visibility = View.GONE
            feedback_recyclerView.visibility = View.VISIBLE
            feedback_new_message.visibility = View.VISIBLE
        } else {
            feedback_progress_bar.visibility = View.VISIBLE
            feedback_recyclerView.visibility = View.INVISIBLE
            feedback_new_message.visibility = View.INVISIBLE
        }
    }
}