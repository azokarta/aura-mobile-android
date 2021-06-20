package kz.aura.merp.employee.view

interface PermissionsListener {
    fun sendResultOfRequestLocation(granted: Boolean)
    fun sendResultOfEnableLocation(granted: Boolean)
}