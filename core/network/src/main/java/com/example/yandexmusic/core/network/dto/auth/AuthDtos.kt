package com.example.yandexmusic.core.network.dto.auth

import com.google.gson.annotations.SerializedName

/** Ответ multi_step/start */
data class AuthStartResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("can_authorize") val canAuthorize: Boolean?,
    @SerializedName("track_id") val trackId: String?,
    @SerializedName("preferred_auth_method") val preferredAuthMethod: String?,
    @SerializedName("auth_methods") val authMethods: List<String>?,
    @SerializedName("csrf_token") val csrfToken: String?,
    @SerializedName("can_register") val canRegister: Boolean?
)

/** Ответ multi_step/commit_password */
data class AuthPasswordResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("redirect_url") val redirectUrl: String?,
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("account") val account: AuthAccount?
)

data class AuthAccount(
    @SerializedName("login") val login: String?,
    @SerializedName("uid") val uid: Long?,
    @SerializedName("display_name") val displayName: DisplayName?
)

data class DisplayName(
    @SerializedName("name") val name: String?
)

/** Ответ textcaptcha */
data class CaptchaResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("key") val key: String?,
    @SerializedName("image_url") val imageUrl: String?
)

/** Ответ challenge/submit */
data class ChallengeSubmitResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("challenge") val challenge: ChallengeInfo?
)

data class ChallengeInfo(
    @SerializedName("challengeType") val challengeType: String?,
    @SerializedName("availableChallenges") val availableChallenges: List<String>?,
    @SerializedName("phoneId") val phoneId: Long?
)

/** Ответ phone-confirm-code-submit */
data class SmsCodeSubmitResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("code_length") val codeLength: Int?,
    @SerializedName("deny_resend_until") val denyResendUntil: Long?,
    @SerializedName("number") val number: MaskedNumber?,
    @SerializedName("errors") val errors: List<String>?
)

data class MaskedNumber(
    @SerializedName("masked_international") val maskedInternational: String?
)

/** Универсальный ответ status + errors */
data class StatusResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("errors") val errors: List<String>?
)

/** Ответ token_by_sessionid */
data class XTokenResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Long?
)

/** Ответ oauth /1/token — Music Token */
data class MusicTokenResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Long?,
    @SerializedName("uid") val uid: Long?,
    @SerializedName("error") val error: String?
)
