package kz.aura.merp.customer.presenters

import kz.aura.merp.customer.models.Auth
import kz.aura.merp.customer.services.AuthorizationActivityApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.views.IAuthorizationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IAuthorizationActivityPresenter {
    fun signIn(phoneNumber: String, sn: Int)
}


class AuthorizationActivityPresenter(val iAuthView: IAuthorizationView):
    IAuthorizationActivityPresenter {


    override fun signIn(phoneNumber: String, sn: Int) {
        val apiService = ServiceBuilder.buildService(AuthorizationActivityApi::class.java)
        val callSignIn = apiService.signIn(phoneNumber, sn)

        callSignIn.enqueue(object : Callback<Auth> {
            override fun onFailure(call: Call<Auth>, t: Throwable) {
                iAuthView.onError(t.message.toString())
            }

            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                iAuthView.onSuccess()


            }

        })
    }

}