package com.mihaialexandruteodor406.unibucnavigator.ui.model

import com.google.gson.annotations.SerializedName

data class MarkerData(

    @SerializedName("waypointName") val waypointName: String,
    @SerializedName("waypointDescript") val waypointDescript: String,
    @SerializedName("lat") val lat: Double ,
    @SerializedName("lon") val lon: Double
)