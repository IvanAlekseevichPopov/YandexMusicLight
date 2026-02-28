package com.example.yandexmusic.core.data

import android.util.Log
import com.example.yandexmusic.core.network.AuthApiClient
import com.example.yandexmusic.core.network.dto.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val tokenStorage: TokenStorage) {

    companion object {
        private const val TAG = "AuthRepository"

        private const val X_TOKEN_CLIENT_ID = "c0ebe342af7d48fbbbfcf2d2eedb8f9e"
        private const val X_TOKEN_CLIENT_SECRET = "ad0a908f0aa341a182a37ecd75bc319e"
        private const val MUSIC_CLIENT_ID = "23cabbbdc6cd418abb4b39c32c41195d"
        private const val MUSIC_CLIENT_SECRET = "53bc75238f0c4d08a118e51fe9203300"

        private const val X_TOKEN_URL = "https://mobileproxy.passport.yandex.net/1/bundle/oauth/token_by_sessionid"
        private const val MUSIC_TOKEN_URL = "https://oauth.mobile.yandex.net/1/token"
    }

    private val passportApi = AuthApiClient.passportApi
    private val tokenApi = AuthApiClient.tokenApi

    private var csrfToken: String = ""
    private var trackId: String = ""

    /** Шаг 1: Получить CSRF-токен из HTML */
    suspend fun fetchCsrfToken(): Result<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "fetchCsrfToken: starting")
        try {
            AuthApiClient.clearCookies()
            val response = passportApi.getAuthPage()
            val html = response.string()
            val regex = """csrf_token"[^"]*value="([^"]*)"""".toRegex()
            val match = regex.find(html)
            val token = match?.groupValues?.get(1)
                ?: return@withContext Result.failure(Exception("CSRF-токен не найден в HTML"))

            csrfToken = token
            Log.d(TAG, "fetchCsrfToken: success, token=${token.take(20)}...")
            Result.success(token)
        } catch (e: Exception) {
            Log.e(TAG, "fetchCsrfToken: error", e)
            Result.failure(e)
        }
    }

    /** Шаг 2: Отправить логин, получить track_id */
    suspend fun submitLogin(login: String): Result<AuthStartResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "submitLogin: login=$login")
        try {
            val response = passportApi.authStart(csrfToken, login)
            Log.d(TAG, "submitLogin: status=${response.status}, trackId=${response.trackId}, canRegister=${response.canRegister}")

            if (response.canRegister == true) {
                return@withContext Result.failure(Exception("Аккаунт не найден"))
            }

            response.trackId?.let { trackId = it }
                ?: return@withContext Result.failure(Exception("track_id не получен"))

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "submitLogin: error", e)
            Result.failure(e)
        }
    }

    /** Шаг 3: Отправить пароль */
    suspend fun submitPassword(password: String): Result<AuthPasswordResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "submitPassword: trackId=$trackId")
        try {
            val response = passportApi.commitPassword(csrfToken, trackId, password)
            Log.d(TAG, "submitPassword: status=${response.status}, state=${response.state}, errors=${response.errors}")

            if (response.errors?.contains("password.not_matched") == true) {
                return@withContext Result.failure(Exception("Неверный пароль"))
            }

            if (response.status != "ok") {
                val errorMsg = response.errors?.joinToString(", ") ?: "Неизвестная ошибка"
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "submitPassword: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Получить капчу */
    suspend fun getCaptcha(): Result<CaptchaResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "getCaptcha: trackId=$trackId")
        try {
            val response = passportApi.getTextCaptcha(csrfToken, trackId)
            Log.d(TAG, "getCaptcha: status=${response.status}, key=${response.key}")

            if (response.status != "ok" || response.key == null) {
                return@withContext Result.failure(Exception("Не удалось получить капчу"))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "getCaptcha: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Отправить ответ на капчу */
    suspend fun submitCaptcha(key: String, answer: String): Result<StatusResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "submitCaptcha: key=$key, answer=$answer")
        try {
            val response = passportApi.checkHuman(csrfToken, trackId, key, answer)
            Log.d(TAG, "submitCaptcha: status=${response.status}, errors=${response.errors}")

            if (response.status != "ok") {
                val errorMsg = if (response.errors?.contains("captcha.not_matched") == true) {
                    "Неверная капча"
                } else {
                    response.errors?.joinToString(", ") ?: "Ошибка проверки капчи"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "submitCaptcha: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Получить информацию о challenge */
    suspend fun submitChallenge(): Result<ChallengeSubmitResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "submitChallenge: trackId=$trackId")
        try {
            val response = passportApi.challengeSubmit(csrfToken, trackId)
            Log.d(TAG, "submitChallenge: status=${response.status}, type=${response.challenge?.challengeType}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "submitChallenge: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Запросить SMS-код */
    suspend fun requestSmsCode(): Result<SmsCodeSubmitResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "requestSmsCode: trackId=$trackId")
        try {
            val response = passportApi.requestSmsCode(csrfToken, trackId)
            Log.d(TAG, "requestSmsCode: status=${response.status}, codeLength=${response.codeLength}, phone=${response.number?.maskedInternational}")

            if (response.status != "ok") {
                val errorMsg = response.errors?.joinToString(", ") ?: "Не удалось отправить SMS"
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "requestSmsCode: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Проверить SMS-код */
    suspend fun verifySmsCode(code: String): Result<StatusResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "verifySmsCode: code=$code")
        try {
            val response = passportApi.verifySmsCode(csrfToken, trackId, code)
            Log.d(TAG, "verifySmsCode: status=${response.status}, errors=${response.errors}")

            if (response.status != "ok") {
                val errorMsg = response.errors?.joinToString(", ") ?: "Неверный код"
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "verifySmsCode: error", e)
            Result.failure(e)
        }
    }

    /** Flow B: Завершить challenge, получить session cookies */
    suspend fun commitSmsCode(): Result<StatusResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "commitSmsCode: trackId=$trackId")
        try {
            val response = passportApi.commitSmsCode(csrfToken, trackId)
            Log.d(TAG, "commitSmsCode: status=${response.status}, errors=${response.errors}")

            if (response.status != "ok") {
                val errorMsg = response.errors?.joinToString(", ") ?: "Ошибка завершения challenge"
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "commitSmsCode: error", e)
            Result.failure(e)
        }
    }

    /** Шаги 4-5: Session cookies → X-Token → Music Token */
    suspend fun exchangeTokens(): Result<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "exchangeTokens: starting")
        try {
            // Извлекаем session cookies
            val sessionCookies = AuthApiClient.getSessionCookies()
                ?: return@withContext Result.failure(Exception("Session cookies не найдены"))

            val cookieHeader = "Session_id=${sessionCookies.sessionId}; sessionid2=${sessionCookies.sessionId2}; yandex_login=${sessionCookies.yandexLogin}"

            // Шаг 4: Session → X-Token
            Log.d(TAG, "exchangeTokens: getting X-Token")
            val xTokenResponse = tokenApi.getXToken(
                url = X_TOKEN_URL,
                yaClientHost = "passport.yandex.ru",
                yaClientCookie = cookieHeader,
                clientId = X_TOKEN_CLIENT_ID,
                clientSecret = X_TOKEN_CLIENT_SECRET
            )

            val xToken = xTokenResponse.accessToken
                ?: return@withContext Result.failure(Exception("X-Token не получен: status=${xTokenResponse.status}"))

            Log.d(TAG, "exchangeTokens: X-Token received, expires_in=${xTokenResponse.expiresIn}")
            tokenStorage.xToken = xToken

            // Шаг 5: X-Token → Music Token
            Log.d(TAG, "exchangeTokens: getting Music Token")
            val musicTokenResponse = tokenApi.getMusicToken(
                url = MUSIC_TOKEN_URL,
                grantType = "x-token",
                accessToken = xToken,
                clientId = MUSIC_CLIENT_ID,
                clientSecret = MUSIC_CLIENT_SECRET
            )

            val musicToken = musicTokenResponse.accessToken
                ?: return@withContext Result.failure(
                    Exception("Music Token не получен: error=${musicTokenResponse.error}")
                )

            Log.d(TAG, "exchangeTokens: Music Token received, uid=${musicTokenResponse.uid}, expires_in=${musicTokenResponse.expiresIn}")
            tokenStorage.musicToken = musicToken
            musicTokenResponse.uid?.let { tokenStorage.uid = it }

            Result.success(musicToken)
        } catch (e: Exception) {
            Log.e(TAG, "exchangeTokens: error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        tokenStorage.clear()
        AuthApiClient.clearCookies()
        csrfToken = ""
        trackId = ""
        Log.d(TAG, "logout: done")
    }
}
