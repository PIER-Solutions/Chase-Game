package com.game.chase.core.util.log.impl

import android.util.Log
import com.game.chase.BuildConfig
import com.game.chase.core.util.log.LogWrapper
import retrofit2.Call
import javax.inject.Inject

internal class AndroidLogWrapper @Inject constructor() : LogWrapper {

    private fun findRelevantStackTraceElement(): StackTraceElement? {
        return Thread.currentThread().stackTrace.firstOrNull { element ->
            val className = element.className
            className.startsWith("com.game.chase") &&
                    !className.contains(".log") &&
                    !className.contains("DefaultLogX") &&
                    !className.contains("LogX")
        }
    }

    private fun classNameMethodNameAndLineNumber(): String {
        val ste = findRelevantStackTraceElement()
        return ste?.let {
            val fileName = it.fileName ?: "Unknown.kt"
            val lineNumber = if (it.lineNumber >= 0) it.lineNumber else "?"
            val methodName = if (it.methodName == "invokeSuspend") {
                val regex = Regex("""\$([a-zA-Z0-9_]+)(?:\$[0-9]+)?\$?\d*""")
                val match = regex.find(it.className)
                val outerMethod = match?.groups?.get(1)?.value ?: "unknown"
                "$outerMethod().invokeSuspend()"
            } else {
                "${it.methodName}()"
            }
            "($fileName:$lineNumber)-$methodName"
        } ?: "(Unknown.kt:?)-unknownMethod()"
    }

    private fun formatLogMessage(cml: String, msg: String?): String {
        return "$cml ${msg ?: "Message was null"}"
    }

    private fun buildBreadCrumb(vararg parts: String): String = parts.joinToString(" | ")

    private fun logInternal(priority: Int, tag: String, message: String?, t: Throwable? = null) {
        if (!BuildConfig.DEBUG) return

        val cml = classNameMethodNameAndLineNumber()
        val formatted = formatLogMessage(cml, message)

        when (priority) {
            Log.VERBOSE -> Log.v(tag, formatted, t)
            Log.DEBUG -> Log.d(tag, formatted, t)
            Log.INFO -> Log.i(tag, formatted, t)
            Log.WARN -> Log.w(tag, formatted, t)
            Log.ERROR -> Log.e(tag, formatted, t)
            else -> Log.e(tag, "[Unknown Priority] $formatted", t)
        }
    }

    override fun log(priority: Int, tag: String, msg: String?) {
        logInternal(priority, tag, msg)
    }

    override fun log(priority: Int, tag: String, msg: String, t: Throwable) {
        logInternal(priority, tag, msg, t)
    }

    override fun <T> logNetworkFailure(call: Call<T>, t: Throwable) {
        logAction(classNameMethodNameAndLineNumber(), "requestUrl", call.request().toString())
        logInternal(Log.ERROR, "App", t.message, t)
    }

    override fun logException(e: Exception) = logException(e as Throwable)

    override fun logException(t: Throwable) {
        logInternal(Log.ERROR, "App", t.message, t)
    }

    override fun logException(message: String) {
        logException(Exception(message))
    }

    override fun dropBreadCrumb(category: String, action: String, label: String, value: String) {
        logInternal(Log.INFO, "BreadCrumb", buildBreadCrumb(category, action, label, value))
    }

    override fun logAction(category: String, action: String, label: String) {
        logInternal(Log.INFO, "BreadCrumb", buildBreadCrumb(category, action, label))
    }

    override fun logAction(category: String, action: String, label: String, value: String) {
        logInternal(Log.INFO, "BreadCrumb", buildBreadCrumb(category, action, label, value))
    }

    override fun logNetworkResponseFromBaseClass(category: String, action: String, label: String, value: String) {
        val breadcrumb = buildBreadCrumb(category, classNameMethodNameAndLineNumber(), action, label, value)
        logInternal(Log.INFO, "BreadCrumb", breadcrumb)
    }

    override fun safeAssertShouldNotReachHere(reason: String) {
        val assertionError = AssertionError(reason)
        logException(assertionError)
        if (BuildConfig.DEBUG) throw assertionError
    }
}
