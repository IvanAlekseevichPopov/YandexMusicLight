package com.example.yandexmusic.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "yandex_music_auth",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var musicToken: String?
        get() = prefs.getString(KEY_MUSIC_TOKEN, null)
        set(value) { prefs.edit().putString(KEY_MUSIC_TOKEN, value).apply() }

    var xToken: String?
        get() = prefs.getString(KEY_X_TOKEN, null)
        set(value) { prefs.edit().putString(KEY_X_TOKEN, value).apply() }

    var uid: Long
        get() = prefs.getLong(KEY_UID, 0L)
        set(value) { prefs.edit().putLong(KEY_UID, value).apply() }

    val isLoggedIn: Boolean get() = !musicToken.isNullOrBlank()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_MUSIC_TOKEN = "music_token"
        private const val KEY_X_TOKEN = "x_token"
        private const val KEY_UID = "uid"
    }
}
