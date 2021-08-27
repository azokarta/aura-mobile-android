package kz.aura.merp.employee.data.finance.reference

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.GET

interface ReferenceService {

    @GET("/references/call_directions")
    suspend fun fetchCallDirections(): Response<ResponseHelper<List<CallDirection>>>

    @GET("/references/call_statuses")
    suspend fun fetchCallStatuses(): Response<ResponseHelper<List<CallStatus>>>

    @GET("/references/business_process_statuses")
    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<List<BusinessProcessStatus>>>

    @GET("/references/collect_money_results")
    suspend fun fetchPlanResults(): Response<ResponseHelper<List<PlanResult>>>

    @GET("/references/banks")
    suspend fun fetchBanks(): Response<ResponseHelper<List<Bank>>>

    @GET("/references/payment_methods")
    suspend fun fetchPaymentMethods(): Response<ResponseHelper<List<PaymentMethod>>>

}