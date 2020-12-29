package kz.aura.merp.customer.views

interface IAuthorizationView : BaseView {
    fun onSuccessTransactionId(transactionId: String)
    fun onSuccessAuth()
}