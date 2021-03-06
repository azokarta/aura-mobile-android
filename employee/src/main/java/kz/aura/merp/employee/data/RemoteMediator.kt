//package kz.aura.merp.employee.data
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import kz.aura.merp.employee.data.database.AppDatabase
//import kz.aura.merp.employee.data.database.entities.PlansEntity
//import retrofit2.HttpException
//import java.io.IOException
//
//@OptIn(ExperimentalPagingApi::class)
//class RemoteMediator(
//    private val query: String,
//    private val database: AppDatabase
//) : RemoteMediator<Int, PlansEntity>() {
//    val plansDao = database.plansDao()
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, PlansEntity>
//    ): MediatorResult {
//        return try {
//            // The network load method takes an optional after=<user.id>
//            // parameter. For every page after the first, pass the last user
//            // ID to let it continue from where it left off. For REFRESH,
//            // pass null to load the first page.
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> null
//                // In this example, you never need to prepend, since REFRESH
//                // will always load the first page in the list. Immediately
//                // return, reporting end of pagination.
//                LoadType.PREPEND ->
//                    return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val lastItem = state.lastItemOrNull()
//
//                    // You must explicitly check if the last item is null when
//                    // appending, since passing null to networkService is only
//                    // valid for initial load. If lastItem is null it means no
//                    // items were loaded after the initial REFRESH and there are
//                    // no more items to load.
//                    if (lastItem == null) {
//                        return MediatorResult.Success(
//                            endOfPaginationReached = true
//                        )
//                    }
//
//                    lastItem.id
//                }
//            }
//
//            // Suspending network load via Retrofit. This doesn't need to be
//            // wrapped in a withContext(Dispatcher.IO) { ... } block since
//            // Retrofit's Coroutine CallAdapter dispatches on a worker
//            // thread.
//            val response = networkService.searchUsers(
//                query = query, after = loadKey
//            )
//
//            database.withTransaction {
//                if (loadType == LoadType.REFRESH) {
//                    userDao.deleteByQuery(query)
//                }
//
//                // Insert new users into database, which invalidates the
//                // current PagingData, allowing Paging to present the updates
//                // in the DB.
//                userDao.insertAll(response.users)
//            }
//
//            MediatorResult.Success(
//                endOfPaginationReached = response.nextKey == null
//            )
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//    }
//
//
//}