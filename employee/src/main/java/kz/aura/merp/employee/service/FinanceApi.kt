package kz.aura.merp.employee.service

import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.data.model.ResponseHelper
import kz.aura.merp.employee.data.model.TrackEmpProcess
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {
    @GET("/ma-collect-money/collector/{collectorId}")
    suspend fun fetchClients(
        @Path("collectorId") collectorId: Long
    ): Response<ResponseHelper<ArrayList<Client>>>

    @GET("/ma-track-emp-process/collectmoney/{maCollectMoneyId}")
    suspend fun fetchTrackEmpProcessCollectMoney(
        @Path("maCollectMoneyId") maCollectMoneyId: Long
    ): Response<ResponseHelper<ArrayList<TrackEmpProcess>>>

    @PUT("/ma-collect-money/modify")
    suspend fun updateClient(@Body client: Client): Response<ResponseHelper<Client>>

    @POST("/ma-track-emp-process/collectmoney")
    suspend fun updateBusinessProcessStep(@Body trackEmpProcess: TrackEmpProcess): Response<ResponseHelper<Any>>
}