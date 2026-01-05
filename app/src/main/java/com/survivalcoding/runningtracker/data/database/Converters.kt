package com.survivalcoding.runningtracker.data.database

import androidx.room.TypeConverter
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromLocationPointList(value: List<LocationPoint>): String {
        val jsonArray = JSONArray()
        value.forEach { point ->
            val jsonObject = JSONObject()
            jsonObject.put("latitude", point.latitude)
            jsonObject.put("longitude", point.longitude)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toLocationPointList(value: String): List<LocationPoint> {
        val list = mutableListOf<LocationPoint>()
        val jsonArray = JSONArray(value)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            list.add(
                LocationPoint(
                    latitude = jsonObject.getDouble("latitude"),
                    longitude = jsonObject.getDouble("longitude")
                )
            )
        }
        return list
    }
}
