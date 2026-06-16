package com.kitsune.app.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

class StorageHelper(private val context: Context) {

    fun persistUriPermission(uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    fun validateAndCreateStructure(rootUriString: String): Boolean {
        return try {
            val rootUri = Uri.parse(rootUriString)
            val rootDoc = DocumentFile.fromTreeUri(context, rootUri) ?: return false

            if (!rootDoc.exists() || !rootDoc.isDirectory) return false

            // Check and create subfolders
            val subFolders = listOf("Comics", "Videos", "Backup", "Cache")
            subFolders.forEach { folderName ->
                val folder = rootDoc.findFile(folderName)
                if (folder == null || !folder.isDirectory) {
                    rootDoc.createDirectory(folderName)
                }
            }

            // Create .nomedia in root
            if (rootDoc.findFile(".nomedia") == null) {
                rootDoc.createFile("*/*", ".nomedia")
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    fun isUriPermissionValid(uriString: String?): Boolean {
        if (uriString.isNullOrEmpty()) return false
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.persistedUriPermissions.any {
                it.uri == uri && it.isReadPermission && it.isWritePermission
            }
        } catch (e: Exception) {
            false
        }
    }
}
