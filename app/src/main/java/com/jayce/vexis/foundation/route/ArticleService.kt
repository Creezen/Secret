package com.jayce.vexis.foundation.route

import com.creezen.commontool.bean.ArticleBean
import com.creezen.commontool.bean.SectionRemarkBean
import com.jayce.vexis.core.base.BaseService
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ArticleService : BaseService {
    @POST("/postSynergy")
    @FormUrlEncoded
    fun postSynergy(
        @Field("articleTitle") title: String,
        @Field("paragraphs") paragraphs: List<String>,
        @Field("userID") userID: String
    ): Call<Boolean>

    @POST("/getSynergy")
    fun getArticle(): Call<ArrayList<ArticleBean>>

    @POST("/getSection")
    @FormUrlEncoded
    fun getSection(
        @Field("articleId") articleId: Long
    ): Call<ArrayList<SectionRemarkBean>>

    @POST("/postCommen")
    @FormUrlEncoded
    fun postCommen(
        @Field("articleId") articleId: Long,
        @Field("paragraphId") paragraphId: Long,
        @Field("userId") userId: String,
        @Field("comment") comment: String,
    ): Call<Boolean>
}