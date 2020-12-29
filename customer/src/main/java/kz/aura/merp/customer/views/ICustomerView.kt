package kz.aura.merp.customer.views

import kz.aura.merp.customer.data.model.Customer

interface ICustomerView : BaseView {
    fun onSuccess(data: Customer)
}