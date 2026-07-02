package com.jayce.vexis.domain.route

import com.jayce.vexis.util.bean.ArticleBean
import com.jayce.vexis.util.bean.SectionRemarkBean
import com.jayce.vexis.core.base.BaseService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ArticleService : BaseService {

    @POST("/postArticle")
    @Multipart
    fun postArticle(
        @Part("articleTitle") title: String,
        @Part("userID") userID: String,
        @Part("contents") contents: String,
        @Part articleFile: List<MultipartBody.Part>,
    ): Call<Boolean>

    @POST("/getArticle")
    fun getArticle(): Call<ArrayList<ArticleBean>>

    @POST("/getSection")
    @FormUrlEncoded
    fun getSection(
        @Field("articleId") articleId: Long,
    ): Call<ArrayList<SectionRemarkBean>>

    @POST("/postRemark")
    @FormUrlEncoded
    fun postRemark(
        @Field("sectionId") sectionId: Long,
        @Field("userId") userId: String,
        @Field("content") content: String,
        @Field("type") type: Int
    ): Call<Boolean>

    @POST("/deleteArticle")
    @FormUrlEncoded
    fun deleteArticle(@Field("articleId") articleId: Long): Call<Boolean>
}