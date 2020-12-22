package kz.aura.merp.customer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.models.Feedback
import kz.aura.merp.customer.utils.Helpers.dateFormatter
import com.example.aura.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_row.view.*
import kotlinx.android.synthetic.main.sub_comment.view.*

class FeedbackAdapter(private val feedbacks: ArrayList<Feedback>, private val filterComments: ArrayList<Feedback>) : RecyclerView.Adapter<FeedbackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.comment_row, parent, false)
        return FeedbackViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback: Feedback = filterComments[position]
        if (feedback.parentId == 0.toLong() || feedback.parentId == null) {
            holder.view.comment_user_title.text = feedback.customerFio
            holder.view.comment_text.text = feedback.feedbackText
            holder.view.comment_date.text = dateFormatter(feedback.feedbackDate)
            val image = "https://png.pngtree.com/element_our/png/20181206/users-vector-icon-png_260862.jpg"
            Picasso.get()
                .load(image)
                .into(holder.view.comment_user_image)
            val employeeComments = feedbacks.filter { it.parentId == feedback.id }
            holder.view.child_comments_recycler_view.layoutManager = LinearLayoutManager(holder.view.context)
            holder.view.child_comments_recycler_view.adapter = FeedbackEmployeeAdapter(
                employeeComments as ArrayList<Feedback>
            )
        }
    }

    override fun getItemCount(): Int {
        return filterComments.size
    }
}

class FeedbackViewHolder(val view: View) : RecyclerView.ViewHolder(view)


// For Employee -------------------------------------------------------------------------------------------------------------------------------


class FeedbackEmployeeAdapter(private val feedbacks: ArrayList<Feedback>) : RecyclerView.Adapter<FeedbackEmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackEmployeeViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.sub_comment, parent, false)
        return FeedbackEmployeeViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FeedbackEmployeeViewHolder, position: Int) {
        val feedback: Feedback = feedbacks[position]
        holder.view.sub_comment_user_title.text = feedback.customerFio
        holder.view.sub_comment_text.text = feedback.feedbackText
        holder.view.sub_comment_date.text = dateFormatter(feedback.feedbackDate)
        val image = "https://png.pngtree.com/element_our/png/20181206/users-vector-icon-png_260862.jpg"
        Picasso.get()
            .load(image)
            .into(holder.view.sub_comment_user_image)
    }

    override fun getItemCount(): Int = feedbacks.size
}

class FeedbackEmployeeViewHolder(val view: View) : RecyclerView.ViewHolder(view)