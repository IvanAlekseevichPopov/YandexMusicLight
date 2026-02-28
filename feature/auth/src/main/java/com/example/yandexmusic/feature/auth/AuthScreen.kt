package com.example.yandexmusic.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // При успешной авторизации — callback наверх
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess((uiState as AuthUiState.Success).token)
        }
    }

    when (val state = uiState) {
        is AuthUiState.Login -> LoginScreen(
            state = state,
            onLoginChanged = viewModel::onLoginChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onLoginClick = viewModel::login
        )

        is AuthUiState.Captcha,
        is AuthUiState.SmsVerification,
        is AuthUiState.ExchangingTokens -> ChallengeScreen(
            state = state,
            onCaptchaAnswerChanged = viewModel::onCaptchaAnswerChanged,
            onSubmitCaptcha = viewModel::submitCaptcha,
            onRefreshCaptcha = viewModel::refreshCaptcha,
            onSmsCodeChanged = viewModel::onSmsCodeChanged,
            onSubmitSmsCode = viewModel::submitSmsCode,
            onResendSmsCode = viewModel::resendSmsCode
        )

        is AuthUiState.Success -> {
            // Обработано в LaunchedEffect
        }
    }
}
