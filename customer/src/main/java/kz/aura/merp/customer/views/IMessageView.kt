package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.Message

interface IMessageView : BaseView {
    fun onSuccess(messages: ArrayList<Message>)
}