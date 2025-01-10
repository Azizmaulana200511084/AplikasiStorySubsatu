package com.aziz.aplikasistorysubsatu.data.remote.network

import com.aziz.aplikasistorysubsatu.data.remote.response.login.LoginResponse
import com.aziz.aplikasistorysubsatu.data.remote.response.signup.SignUpResponse
import com.aziz.aplikasistorysubsatu.data.remote.response.stories.PostStoryResponse
import com.aziz.aplikasistorysubsatu.data.remote.response.stories.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun postSignUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ) : StoriesResponse


    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): PostStoryResponse
}