package kz.aura.merp.employee.service

import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.model.ResponseHelper
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.data.model.TrackEmpProcessServiceApplication
import retrofit2.Response
import retrofit2.http.*

interface MasterApi {
    @GET("/service-application/master/{masterId}")
    suspend fun fetchApplications(@Path("masterId") masterId: Long): Response<ResponseHelper<ArrayList<ServiceApplication>>>

    @GET("/ma-track-emp-process/serviceApplication/{serviceApplicationId}")
    suspend fun fetchTrackEmpProcessServiceApplication(
        @Path("serviceApplicationId") serviceApplicationId: Long
    ): Response<ResponseHelper<ArrayList<TrackEmpProcessServiceApplication>>>

    @PUT("/service-application/modify")
    suspend fun updateServiceApplication(@Body serviceApplication: ServiceApplication): Response<ResponseHelper<ServiceApplication>>

    @POST("/ma-track-emp-process/service")
    suspend fun updateBusinessProcessStep(@Body trackEmpProcess: TrackEmpProcess): Response<ResponseHelper<Any>>
}