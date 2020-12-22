package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.Feedback

interface IFeedbackView : BaseView {
    fun onSuccess(feedback: ArrayList<Feedback>)
}