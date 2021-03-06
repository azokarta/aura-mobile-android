package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.*

interface ReferenceApi {
    @GET("/api/reference/ma-tracked-business-processes")
    suspend fun getTrackedBusinessProcesses(): Response<ResponseHelper<ArrayList<TrackedBusinessProcess>>>

    @GET("/ma-track-step-orders/business-process/{bpid}")
    suspend fun getTrackStepOrdersBussinessProcesses(
        @Path("bpid") bpid: Int
    ): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>>

    @GET("/api/reference/crm-demo-result")
    suspend fun getDemoResults(): Response<ResponseHelper<ArrayList<DemoResult>>>

    @GET("/api/reference/service-application-status")
    suspend fun getServiceApplicationStatus(): Response<ResponseHelper<ArrayList<ServiceApplicationStatus>>>

    @POST("/ma-track-emp-process/staff/{staffId}")
    suspend fun newStaffLocation(@Body staffLocation: StaffLocation): Response<ResponseHelper<Any>>

    @GET("/api/reference/contract-type")
    suspend fun getContractTypes(@Query("staffId") staffId: Long): Response<ResponseHelper<ArrayList<ContractType>>>
}