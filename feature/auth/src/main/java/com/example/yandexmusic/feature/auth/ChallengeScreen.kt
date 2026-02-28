package com.example.yandexmusic.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ChallengeScreen(
    state: AuthUiState,
    onCaptchaAnswerChanged: (String) -> Unit,
    onSubmitCaptcha: () -> Unit,
    onRefreshCaptcha: () -> Unit,
    onSmsCodeChanged: (String) -> Unit,
    onSubmitSmsCode: () -> Unit,
    onResendSmsCode: () -> Unit
) {
    when (state) {
        is AuthUiState.Captcha -> CaptchaContent(
            state = state,
            onAnswerChanged = onCaptchaAnswerChanged,
            onSubmit = onSubmitCaptcha,
            onRefresh = onRefreshCaptcha
        )

        is AuthUiState.SmsVerification -> SmsContent(
            state = state,
            onCodeChanged = onSmsCodeChanged,
            onSubmit = onSubmitSmsCode,
            onResend = onResendSmsCode
        )

        is AuthUiState.ExchangingTokens -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun CaptchaContent(
    state: AuthUiState.Captcha,
    onAnswerChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Подтверждение",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Введите текст с картинки",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Captcha image
        AsyncImage(
            model = state.imageUrl,
            contentDescription = "Капча",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onRefresh, enabled = !state.isLoading) {
            Text("Обновить капчу")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.answer,
            onValueChange = onAnswerChanged,
            label = { Text("Ответ") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            keyboardActions = KeyboardActions(
                onDone = { if (!state.isLoading) onSubmit() }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Подтвердить")
            }
        }

        state.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SmsContent(
    state: AuthUiState.SmsVerification,
    onCodeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onResend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Код подтверждения",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SMS отправлена на ${state.maskedPhone}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.code,
            onValueChange = { value ->
                if (value.length <= state.codeLength && value.all { it.isDigit() }) {
                    onCodeChanged(value)
                }
            },
            label = { Text("Код из SMS") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (!state.isLoading) onSubmit() }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onResend, enabled = !state.isLoading) {
            Text("Отправить повторно")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Подтвердить")
            }
        }

        state.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
