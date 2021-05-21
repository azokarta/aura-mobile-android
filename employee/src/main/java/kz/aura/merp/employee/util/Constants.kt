package kz.aura.merp.employee.util

object Constants {
    const val YANDEX_MAP_API_KEY = "52e19b27-d221-41e2-887f-aaebcf59000a"

    const val WE_MOB_PROD_AUTH = "http://werp.kz:30001"
    const val WE_MOB_PROD_MAIN = "http://werp.kz:30020"
    const val WE_MOB_PROD_FINANCE = "http://werp.kz:30021"
    const val WE_MOB_PROD_SERVICE = "http://werp.kz:30022"
    const val WE_MOB_PROD_CRM = "http://werp.kz:30023"

    const val WE_MOB_DEV_AUTH = "http://werp.kz:32001"
    const val WE_MOB_DEV_MAIN = "http://werp.kz:32020"
    const val WE_MOB_DEV_FINANCE = "http://werp.kz:32021"
    const val WE_MOB_DEV_SERVICE = "http://werp.kz:32022"
    const val WE_MOB_DEV_CRM = "http://werp.kz:32023"

    const val WE_MOB_TEST_AUTH = "http://werp.kz:31001"
    const val WE_MOB_TEST_MAIN = "http://werp.kz:31020"
    const val WE_MOB_TEST_FINANCE = "http://werp.kz:31021"
    const val WE_MOB_TEST_SERVICE = "http://werp.kz:31022"
    const val WE_MOB_TEST_CRM = "http://werp.kz:31023"


    const val OCR_URL = "https://aurakz.idkit.co"
    val SELECTED_SERVER = Server.DEV

    val crmPositions = arrayListOf(4L, 3L, 10L, 105L)
    val financePositions = arrayListOf(9L)
    val servicePositions = arrayListOf(16L, 17L)


    // ROOM Database
    const val DATABASE_NAME = "wemob_database"
    const val PLANS_TABLE = "plans_table"
    const val CONTRIBUTIONS_TABLE = "contributions_table"
    const val CALLS_TABLE = "calls_table"
}