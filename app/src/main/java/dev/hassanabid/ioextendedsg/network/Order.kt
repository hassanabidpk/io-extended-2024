package com.hassan.myaiapplication

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("day")
    var day: String,
    @SerializedName("foodItem")
    var foodItem: String,
    @SerializedName("price")
    var price: Int
)