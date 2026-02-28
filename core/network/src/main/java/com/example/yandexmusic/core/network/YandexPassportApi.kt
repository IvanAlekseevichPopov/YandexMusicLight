package com.example.yandexmusic.core.network

import com.example.yandexmusic.core.network.dto.auth.*
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Yandex Passport API для авторизации.
 * Base URL: https://passport.yandex.ru/
 */
interface YandexPassportApi {

    /** Шаг 1: Получить HTML-страницу с CSRF-токеном */
    @GET("am")
    suspend fun getAuthPage(
        @Query("app_platform") appPlatform: String = "android"
    ): ResponseBody

    /** Шаг 2: Отправить логин, получить track_id */
    @FormUrlEncoded
    @POST("registration-validations/auth/multi_step/start")
    suspend fun authStart(
        @Field("csrf_token") csrfToken: String,
        @Field("login") login: String
    ): AuthStartResponse

    /** Шаг 3: Отправить пароль */
    @FormUrlEncoded
    @POST("registration-validations/auth/multi_step/commit_password")
    suspend fun commitPassword(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("password") password: String,
        @Field("retpath") retpath: String = "https://passport.yandex.ru/am/finish?status=ok&from=Login"
    ): AuthPasswordResponse

    /** Flow B: Получить текстовую капчу */
    @FormUrlEncoded
    @POST("registration-validations/textcaptcha")
    suspend fun getTextCaptcha(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String
    ): CaptchaResponse

    /** Flow B: Проверить ответ на капчу */
    @FormUrlEncoded
    @POST("registration-validations/checkHuman")
    suspend fun checkHuman(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("key") key: String,
        @Field("answer") answer: String
    ): StatusResponse

    /** Flow B: Получить информацию о challenge */
    @FormUrlEncoded
    @POST("registration-validations/auth/challenge/submit")
    suspend fun challengeSubmit(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("answer") answer: String = "phone_confirmation"
    ): ChallengeSubmitResponse

    /** Flow B: Запросить SMS-код */
    @FormUrlEncoded
    @POST("registration-validations/phone-confirm-code-submit")
    suspend fun requestSmsCode(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("mode") mode: String = "tracked"
    ): SmsCodeSubmitResponse

    /** Flow B: Проверить SMS-код */
    @FormUrlEncoded
    @POST("registration-validations/phone-confirm-code")
    suspend fun verifySmsCode(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("code") code: String,
        @Field("mode") mode: String = "tracked"
    ): StatusResponse

    /** Flow B: Завершить challenge через SMS */
    @FormUrlEncoded
    @POST("registration-validations/multi-step-commit-sms-code")
    suspend fun commitSmsCode(
        @Field("csrf_token") csrfToken: String,
        @Field("track_id") trackId: String,
        @Field("retpath") retpath: String = "https://passport.yandex.ru/am/finish?status=ok&from=Login"
    ): StatusResponse
}
