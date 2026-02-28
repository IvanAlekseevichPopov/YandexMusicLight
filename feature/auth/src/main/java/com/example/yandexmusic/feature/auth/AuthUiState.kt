package com.example.yandexmusic.feature.auth

sealed interface AuthUiState {

    /** Форма входа: логин + пароль */
    data class Login(
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : AuthUiState

    /** Flow B: Ввод капчи */
    data class Captcha(
        val imageUrl: String,
        val captchaKey: String,
        val answer: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : AuthUiState

    /** Flow B: Ввод SMS-кода */
    data class SmsVerification(
        val maskedPhone: String,
        val code: String = "",
        val codeLength: Int = 6,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : AuthUiState

    /** Обмен токенов */
    data class ExchangingTokens(
        val message: String = "Получение токена..."
    ) : AuthUiState

    /** Авторизация завершена */
    data class Success(
        val token: String
    ) : AuthUiState
}
