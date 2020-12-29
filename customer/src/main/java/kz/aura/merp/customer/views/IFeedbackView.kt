package kz.aura.merp.customer.views

import kz.aura.merp.customer.data.model.Feedback

interface IFeedbackView : BaseView {
    fun onSuccess(feedback: ArrayList<Feedback>)
}