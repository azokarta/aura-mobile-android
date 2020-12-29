package kz.aura.merp.customer.views

import kz.aura.merp.customer.data.model.Message

interface IMessageView : BaseView {
    fun onSuccess(messages: ArrayList<Message>)
}