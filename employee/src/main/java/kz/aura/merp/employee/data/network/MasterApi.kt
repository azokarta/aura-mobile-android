package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MasterApi {
    @GET("/applications")
    suspend fun fetchApplications(): Response<ResponseHelper<ArrayList<ServiceApplication>>>

    @GET("/applications/{applicationNumber}/filters")
    suspend fun fetchFilters(
        @Path("applicationNumber") applicationNumber: Long
    ): Response<ResponseHelper<Filters>>

    @GET("/ma-track-emp-process/serviceApplication/{serviceApplicationId}")
    suspend fun fetchTrackEmpProcessServiceApplication(
        @Path("serviceApplicationId") serviceApplicationId: Long
    ): Response<ResponseHelper<ArrayList<TrackEmpProcessServiceApplication>>>

    @PUT("/service-application/modify")
    suspend fun updateServiceApplication(@Body serviceApplication: ServiceApplication): Response<ResponseHelper<ServiceApplication>>

    @POST("/ma-track-emp-process/service")
    suspend fun updateBusinessProcessStep(@Body trackEmpProcess: TrackEmpProcess): Response<ResponseHelper<Any>>
}