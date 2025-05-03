package com.rizqi.wideloc.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object StorageUtils {
    fun copyUriToInternalStorage(context: Context, uri: Uri?, filename: String): File? {
        uri ?: return null
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, filename)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file
        } catch (e: IOException){
            e.printStackTrace()
            null
        }
    }
}