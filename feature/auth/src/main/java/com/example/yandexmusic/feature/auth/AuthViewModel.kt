package com.example.yandexmusic.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmusic.core.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Login())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- Login ---

    fun onLoginChanged(login: String) {
        val current = _uiState.value
        if (current is AuthUiState.Login) {
            _uiState.value = current.copy(login = login, error = null)
        }
    }

    fun onPasswordChanged(password: String) {
        val current = _uiState.value
        if (current is AuthUiState.Login) {
            _uiState.value = current.copy(password = password, error = null)
        }
    }

    fun login() {
        val current = _uiState.value
        if (current !is AuthUiState.Login) return
        if (current.login.isBlank() || current.password.isBlank()) {
            _uiState.value = current.copy(error = "Введите логин и пароль")
            return
        }

        val login = current.login.trim()
        val password = current.password

        _uiState.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // Шаг 1: CSRF
            Log.d(TAG, "login: fetching CSRF token")
            authRepository.fetchCsrfToken().onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = "Ошибка соединения: ${e.message}")
                return@launch
            }

            // Шаг 2: Логин
            Log.d(TAG, "login: submitting login")
            authRepository.submitLogin(login).onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            // Шаг 3: Пароль
            Log.d(TAG, "login: submitting password")
            val passwordResult = authRepository.submitPassword(password)

            passwordResult.onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            val response = passwordResult.getOrNull() ?: return@launch

            if (response.state == "auth_challenge") {
                // Flow B: нужен challenge
                Log.d(TAG, "login: auth_challenge detected, requesting captcha")
                requestCaptcha()
            } else {
                // Flow A: успех, обмениваем токены
                Log.d(TAG, "login: password accepted (Flow A), exchanging tokens")
                exchangeTokens()
            }
        }
    }

    // --- Captcha (Flow B) ---

    fun onCaptchaAnswerChanged(answer: String) {
        val current = _uiState.value
        if (current is AuthUiState.Captcha) {
            _uiState.value = current.copy(answer = answer, error = null)
        }
    }

    fun submitCaptcha() {
        val current = _uiState.value
        if (current !is AuthUiState.Captcha) return
        if (current.answer.isBlank()) {
            _uiState.value = current.copy(error = "Введите текст с картинки")
            return
        }

        _uiState.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // Проверка капчи
            authRepository.submitCaptcha(current.captchaKey, current.answer).onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            // Challenge submit → получаем info
            Log.d(TAG, "submitCaptcha: captcha passed, submitting challenge")
            authRepository.submitChallenge().onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            // Запрашиваем SMS
            Log.d(TAG, "submitCaptcha: requesting SMS code")
            val smsResult = authRepository.requestSmsCode()

            smsResult.onSuccess { smsResponse ->
                _uiState.value = AuthUiState.SmsVerification(
                    maskedPhone = smsResponse.number?.maskedInternational ?: "***",
                    codeLength = smsResponse.codeLength ?: 6
                )
            }.onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun refreshCaptcha() {
        viewModelScope.launch {
            requestCaptcha()
        }
    }

    // --- SMS (Flow B) ---

    fun onSmsCodeChanged(code: String) {
        val current = _uiState.value
        if (current is AuthUiState.SmsVerification) {
            _uiState.value = current.copy(code = code, error = null)
        }
    }

    fun submitSmsCode() {
        val current = _uiState.value
        if (current !is AuthUiState.SmsVerification) return
        if (current.code.length < current.codeLength) {
            _uiState.value = current.copy(error = "Введите ${current.codeLength}-значный код")
            return
        }

        _uiState.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // Проверка SMS-кода
            authRepository.verifySmsCode(current.code).onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            // Завершение challenge
            Log.d(TAG, "submitSmsCode: SMS verified, committing")
            authRepository.commitSmsCode().onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message)
                return@launch
            }

            // Обмен токенов
            Log.d(TAG, "submitSmsCode: challenge completed, exchanging tokens")
            exchangeTokens()
        }
    }

    fun resendSmsCode() {
        val current = _uiState.value
        if (current !is AuthUiState.SmsVerification) return

        _uiState.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            authRepository.requestSmsCode()
                .onSuccess { smsResponse ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        code = "",
                        maskedPhone = smsResponse.number?.maskedInternational ?: current.maskedPhone
                    )
                }
                .onFailure { e ->
                    _uiState.value = current.copy(isLoading = false, error = e.message)
                }
        }
    }

    // --- Internal ---

    private suspend fun requestCaptcha() {
        val captchaResult = authRepository.getCaptcha()

        captchaResult.onSuccess { captchaResponse ->
            _uiState.value = AuthUiState.Captcha(
                imageUrl = captchaResponse.imageUrl ?: "",
                captchaKey = captchaResponse.key ?: ""
            )
        }.onFailure { e ->
            val current = _uiState.value
            if (current is AuthUiState.Login) {
                _uiState.value = current.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun exchangeTokens() {
        _uiState.value = AuthUiState.ExchangingTokens()

        authRepository.exchangeTokens()
            .onSuccess { token ->
                Log.d(TAG, "exchangeTokens: success")
                _uiState.value = AuthUiState.Success(token)
            }
            .onFailure { e ->
                Log.e(TAG, "exchangeTokens: failure", e)
                _uiState.value = AuthUiState.Login(
                    error = "Ошибка получения токена: ${e.message}"
                )
            }
    }
}
