package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.PushNotification
import kz.aura.merp.customer.util.Constants.CONTENT_TYPE
import kz.aura.merp.customer.util.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("/fcm/send")
    fun postNotification(
        @Body notification: PushNotification
    ): Call<ResponseBody>
}