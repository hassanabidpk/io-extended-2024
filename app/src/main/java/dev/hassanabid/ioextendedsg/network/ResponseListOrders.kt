package com.hassan.myaiapplication

import com.google.gson.annotations.SerializedName

data class ResponseListOrders(
    @SerializedName("data")
    var data: List<Order>,
    @SerializedName("page")
    var page: Int,
    @SerializedName("per_page")
    var perPage: Int,
    @SerializedName("total")
    var total: Int,
    @SerializedName(
        "total_pages")
    var totalPages: Int
)