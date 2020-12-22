package kz.aura.merp.customer.views

interface BaseView {
    fun onError(error: Any) {}
    fun hideProgressBar() {}
    fun showProgressBar() {}
}