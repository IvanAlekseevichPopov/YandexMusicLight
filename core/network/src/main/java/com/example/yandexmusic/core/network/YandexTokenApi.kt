package com.example.yandexmusic.core.network

import com.example.yandexmusic.core.network.dto.auth.MusicTokenResponse
import com.example.yandexmusic.core.network.dto.auth.XTokenResponse
import retrofit2.http.*

/**
 * API обмена токенов.
 * Используют разные base URL — передаём полный URL через @Url.
 */
interface YandexTokenApi {

    /** Session cookies → X-Token */
    @FormUrlEncoded
    @POST
    suspend fun getXToken(
        @Url url: String,
        @Header("Ya-Client-Host") yaClientHost: String,
        @Header("Ya-Client-Cookie") yaClientCookie: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): XTokenResponse

    /** X-Token → Music OAuth Token */
    @FormUrlEncoded
    @POST
    suspend fun getMusicToken(
        @Url url: String,
        @Field("grant_type") grantType: String,
        @Field("access_token") accessToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): MusicTokenResponse
}
