package com.craziers.clubpicker.data

import android.content.Context
import org.json.JSONArray
import java.io.IOException

class ClubRepository(private val context: Context) {

    fun getClubs(): List<Club> {
        val clubs = mutableListOf<Club>()
        try {
            val jsonString = context.assets.open("clubs.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                clubs.add(
                    Club(
                        id = jsonObject.getString("id"),
                        name = jsonObject.getString("name"),
                        nation = jsonObject.optString("nation", "Unknown"),
                        league = jsonObject.getString("league"),
                        starRating = jsonObject.optDouble("starRating", 3.0).toFloat(),
                        badgeUrl = jsonObject.getString("badgeUrl"),
                        primaryColor = jsonObject.getString("primaryColor"),
                        secondaryColor = jsonObject.getString("secondaryColor")

                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return clubs
    }
}
