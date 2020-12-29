package kz.aura.merp.customer.views

import kz.aura.merp.customer.data.model.Bonus

interface IBonusView : BaseView {
    fun onSuccess(bonuses: ArrayList<Bonus>)
}