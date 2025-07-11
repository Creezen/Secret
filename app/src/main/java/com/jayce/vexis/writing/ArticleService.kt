package com.jayce.vexis.writing

import com.jayce.vexis.writing.paragraph.ParagraphCommandBean
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ArticleService {
    @POST("/postSynergy")
    @FormUrlEncoded
    fun postSynergy(
        @Field("articleTitle") title: String,
        @Field("paragraphs") paragraphs: List<String>,
        @Field("userID") userID: String
    ): Call<Boolean>

    @POST("/getSynergy")
    fun getArticle(): Call<ArrayList<ArticleBean>>?

    @POST("getArticle")
    @FormUrlEncoded
    fun getParagraphs(
        @Field("synergyId") synergyId: Long,
    ): Call<ArrayList<ParagraphCommandBean>>?

    @POST("postCommen")
    @FormUrlEncoded
    fun postCommen(
        @Field("synergyId") synergyId: Long,
        @Field("paragraphId") paragraphId: Long,
        @Field("userId") userId: String,
        @Field("comment") comment: String,
    ): Call<Boolean>
}
