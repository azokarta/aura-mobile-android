package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.Customer

interface ICustomerView : BaseView {
    fun onSuccess(data: Customer)
}