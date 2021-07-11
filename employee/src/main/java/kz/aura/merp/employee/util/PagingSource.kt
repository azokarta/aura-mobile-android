package kz.aura.merp.employee.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kz.aura.merp.employee.model.Plan
import retrofit2.HttpException
import kotlin.reflect.KSuspendFunction1

class PagingSource(
    val getPlans: KSuspendFunction1<Int, List<Plan>>
) : PagingSource<Int, Plan>() {

    override fun getRefreshKey(state: PagingState<Int, Plan>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Plan> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = getPlans(nextPageNumber)
            println("Res: $response")
            return LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }
    }

}