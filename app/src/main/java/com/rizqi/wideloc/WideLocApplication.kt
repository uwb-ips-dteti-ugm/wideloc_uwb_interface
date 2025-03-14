package com.rizqi.wideloc

import android.app.Application
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WideLocApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setupCrashLogger()
    }

    private fun setupCrashLogger() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val crashLog = getCrashLog(throwable)

            Timber.e(throwable, "App Crashed: %s", crashLog)

            saveCrashToFile(crashLog)

            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }
    }

    private fun getCrashLog(throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return "Time: $timestamp\nException: ${throwable.localizedMessage}\nStackTrace:\n$stackTrace"
    }

    private fun saveCrashToFile(log: String) {
        try {
            val dir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "CrashLogs")
            if (!dir.exists()) dir.mkdirs()

            val logFile = File(dir, "crash_log_${System.currentTimeMillis()}.txt")
            logFile.writeText(log)

            Timber.d("Crash log saved: ${logFile.absolutePath}")
        } catch (e: Exception){
            Timber.e(e, "Failed to save crash log")
        }
    }
}