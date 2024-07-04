package com.hassan.myaiapplication

import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Path

interface ApiInterface {
    @GET("/pastorders")
    suspend fun getPastOrders(): Response<ResponseListOrders>

    @GET("/pastorders/{orderId}")
    suspend fun getUser(@Path("orderId") id: Int): Response<Order>
}