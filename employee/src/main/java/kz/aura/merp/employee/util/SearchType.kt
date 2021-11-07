package kz.aura.merp.employee.util

import kz.aura.merp.employee.R

enum class SearchType(val value: Int) {
    CN(R.string.cn),
    FULL_NAME(R.string.fullname),
    PHONE_NUMBER(R.string.phone_number),
    ADDRESS(R.string.address)
}