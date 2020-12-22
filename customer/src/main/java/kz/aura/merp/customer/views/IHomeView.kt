package kz.aura.merp.customer.views

interface IHomeView : BaseView {
    fun onSuccessUnreadMessagesCount(count: Int)
}