package kz.aura.merp.employee.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kz.aura.merp.employee.model.Plan

class PlansTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun planToString(plan: Plan): String {
        return gson.toJson(plan)
    }

    @TypeConverter
    fun stringToPlan(data: String): Plan {
        val listType = object : TypeToken<Plan>() {}.type
        return gson.fromJson(data, listType)
    }

}