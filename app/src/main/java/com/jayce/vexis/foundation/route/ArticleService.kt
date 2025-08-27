package com.jayce.vexis.foundation.route

import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.foundation.bean.ArticleEntry
import com.jayce.vexis.foundation.bean.ParaRemarkEntry
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
    fun getArticle(): Call<ArrayList<ArticleEntry>>?

    @POST("getArticleFragment")
    @FormUrlEncoded
    fun getParagraphs(
        @Field("synergyId") synergyId: Long,
    ): Call<ArrayList<ParaRemarkEntry>>?

    @POST("postCommen")
    @FormUrlEncoded
    fun postCommen(
        @Field("synergyId") synergyId: Long,
        @Field("paragraphId") paragraphId: Long,
        @Field("userId") userId: String,
        @Field("comment") comment: String,
    ): Call<Boolean>
}