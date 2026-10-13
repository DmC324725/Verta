package com.sloppydev.verta.domain.usecase

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Use case responsible for managing folder persistence (SharedPreferences) and SAF URI permissions.
 */
class ManageFolderPersistenceUseCase(private val context: Context) {

    private val prefs = context.getSharedPreferences("verta_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_URI = "saved_tree_uri"
    }

    /** Returns true if a folder has been previously saved. */
    fun hasSavedFolder(): Boolean = prefs.contains(KEY_SAVED_URI)

    /** Retrieves the saved tree URI string if any. */
    fun getSavedUriString(): String? = prefs.getString(KEY_SAVED_URI, null)

    /** Saves the tree URI persistently and takes persistable read permissions. */
    fun saveFolderUri(uri: Uri, contentResolver: ContentResolver) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some providers do not support persistable permissions
        } catch (_: Exception) {
            // General exception safeguard
        }
        prefs.edit().putString(KEY_SAVED_URI, uri.toString()).apply()
    }

    /** Clears the saved folder URI. */
    fun clearSavedFolder() {
        prefs.edit().remove(KEY_SAVED_URI).apply()
    }

    /** Checks if persistable read permission is still valid for the given URI. */
    fun hasValidPermission(uri: Uri, contentResolver: ContentResolver): Boolean {
        return try {
            contentResolver.persistedUriPermissions.any {
                it.uri == uri && it.isReadPermission
            }
        } catch (_: Exception) {
            false
        }
    }
}
