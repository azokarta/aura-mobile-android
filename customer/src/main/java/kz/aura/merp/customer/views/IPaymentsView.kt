package kz.aura.merp.customer.views

import kz.aura.merp.customer.models.PaymentSchedule

interface IPaymentsView : BaseView {
    fun onSuccessPayments(paymentSchedules: ArrayList<PaymentSchedule>) {}
    fun onSuccessPayment(paymentSchedule: PaymentSchedule) {}
    fun onSuccessPaymentsSchedule(paymentSchedules: ArrayList<PaymentSchedule>) {}
}