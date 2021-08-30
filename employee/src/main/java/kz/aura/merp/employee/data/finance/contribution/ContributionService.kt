package kz.aura.merp.employee.data.finance.contribution

import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.model.ResponseHelper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ContributionService {

    @GET("/collect_moneys")
    suspend fun fetchContributions(): Response<ResponseHelper<List<Contribution>>>

    @GET("/collect_moneys/{contractId}")
    suspend fun fetchContributionsByContractId(@Path("contractId") contractId: Long): Response<ResponseHelper<List<Contribution>>>

}