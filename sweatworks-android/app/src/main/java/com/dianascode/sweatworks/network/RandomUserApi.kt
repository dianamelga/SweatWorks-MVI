package com.dianascode.sweatworks.network

import com.dianascode.sweatworks.models.UserResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("api/")
    fun getUsers(
        @Query("results") results: Int?=null
    ): Observable<UserResponse>

    companion object {
        fun createForApi(apiURL: String) : RandomUserApi {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(SweatWorksClient.provideClient())
                .baseUrl(apiURL)
                .build()

            return retrofit.create(RandomUserApi::class.java)
        }
    }
}