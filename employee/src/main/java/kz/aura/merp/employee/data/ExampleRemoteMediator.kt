package kz.aura.merp.employee.data

//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import kz.aura.merp.employee.data.database.AppDatabase
//import kz.aura.merp.employee.data.database.entities.PlansEntity
//import kz.aura.merp.employee.data.network.FinanceApi
//
//@ExperimentalPagingApi
//class ExampleRemoteMediator(
//    private val database: AppDatabase,
//    private val financeApi: FinanceApi,
//) : RemoteMediator<Int, PlansEntity>() {
//    val plansDao = database.plansDao()
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, PlansEntity>
//    ): MediatorResult {
//        try {
//            // Get the closest item from PagingState that we want to load data around.
//            val loadKey = when (loadType) {
//                REFRESH -> null
//                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//                APPEND -> {
//                    // Query DB for SubredditRemoteKey for the subreddit.
//                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
//                    // receive from the Reddit API to fetch the next or previous page.
//                    val remoteKey = db.withTransaction {
//                        remoteKeyDao.remoteKeyByPost(subredditName)
//                    }
//
//                    // We must explicitly check if the page key is null when appending, since the
//                    // Reddit API informs the end of the list by returning null for page key, but
//                    // passing a null key to Reddit API will fetch the initial page.
//                    if (remoteKey.nextPageKey == null) {
//                        return MediatorResult.Success(endOfPaginationReached = true)
//                    }
//
//                    remoteKey.nextPageKey
//                }
//            }
//
//            val data = redditApi.getTop(
//                subreddit = subredditName,
//                after = loadKey,
//                before = null,
//                limit = when (loadType) {
//                    REFRESH -> state.config.initialLoadSize
//                    else -> state.config.pageSize
//                }
//            ).data
//
//            val items = data.children.map { it.data }
//
//            db.withTransaction {
//                if (loadType == REFRESH) {
//                    postDao.deleteBySubreddit(subredditName)
//                    remoteKeyDao.deleteBySubreddit(subredditName)
//                }
//
//                remoteKeyDao.insert(SubredditRemoteKey(subredditName, data.after))
//                postDao.insertAll(items)
//            }
//
//            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
//        } catch (e: IOException) {
//            return MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            return MediatorResult.Error(e)
//        }
//    }
//}