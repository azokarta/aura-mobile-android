package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface DealerApi {
    @GET("/crm-doc-demo/dealer/{dealerId}")
    suspend fun getAll(
        @Path("dealerId") dealerId: Long
    ): Response<ResponseHelper<ArrayList<Demo>>>

    @GET("/ma-track-emp-process/demo/{demoId}")
    suspend fun getTrackEmpProcessDemo(
        @Path("demoId") demoId: Long
    ): Response<ResponseHelper<ArrayList<TrackEmpProcess>>>

    @PUT("/crm-doc-demo/modify")
    suspend fun updateDemo(@Body demo: Demo): Response<ResponseHelper<Demo>>

    @POST("/ma-track-emp-process/demo")
    suspend fun updateStepBusinessProcess(@Body trackEmpProcess: TrackEmpProcess): Response<ResponseHelper<Any>>

    @POST("/ocr_demo/send_sms")
    suspend fun sendSms(
        @Query("demoId") demoId: Long,
        @Query("phoneCode") phoneCode: String,
        @Query("phoneNumber") phoneNumber: String
    ): Response<ResponseHelper<String>>

    @GET("/crm-doc-demo/{demoId}")
    suspend fun fetchDemoById(@Path("demoId") demoId: Long): Response<ResponseHelper<Demo>>

    @POST("/ocr_demo/update_status")
    suspend fun updateStatus(@Query("demoId") demoId: Long): Response<ResponseHelper<Demo>>
}