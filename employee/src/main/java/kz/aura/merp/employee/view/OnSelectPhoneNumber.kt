package kz.aura.merp.employee.view

interface OnSelectPhoneNumber {
    fun incoming(phoneNumber: String)
    fun outgoing(phoneNumber: String)
}