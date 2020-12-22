package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.Service

interface IServicesView : BaseView {
    fun onSuccessServices(services: ArrayList<Service>) {}
    fun onSuccessService(service: Service) {}
}