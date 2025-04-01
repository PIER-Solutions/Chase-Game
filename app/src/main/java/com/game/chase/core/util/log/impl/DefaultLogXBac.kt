package com.game.chase.core.util.log.impl

import android.util.Log

import com.game.chase.BuildConfig
import com.game.chase.core.util.log.LogX
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import javax.inject.Inject


class DefaultLogXBac  @Inject constructor() : LogX {

    private fun findRelevantStackTraceElement(): StackTraceElement? {
        val stack = Thread.currentThread().stackTrace

        return stack.firstOrNull { element ->
            val className = element.className
            className.startsWith("com.game.chase") &&
                    !className.contains(".log") &&  // exclude all log-related classes, not just log.impl
                    !className.contains("DefaultLogX") &&  // prevent self-ref
                    !className.contains("LogX") // catch interface calls
        }
    }

    /**
     * Returns the class name, method name, and line number from the currently
     * executing log call in the form <class_name>.<method_name>()-<line_number>
     *
     * @return String - String representing class name, method name, and line
     * number.
    </line_number></method_name></class_name> */
    private fun classNameMethodNameAndLineNumber(): String {
        val ste = findRelevantStackTraceElement()
        return ste?.let {
            val fileName = it.fileName ?: "Unknown.kt"
            val lineNumber = if (it.lineNumber >= 0) it.lineNumber else "?"
            val methodName = when {
                it.methodName == "invokeSuspend" -> {
                    // Extract the surrounding function name from the class name
                    val regex = Regex("""\$([a-zA-Z0-9_]+)(?:\$[0-9]+)?\$?\d*""")
                    val match = regex.find(it.className)
                    val outerMethod = match?.groups?.get(1)?.value ?: "unknown"
                    "$outerMethod().invokeSuspend()"
                }
                else -> "${it.methodName}()"
            }
            "($fileName:$lineNumber)-$methodName"
        } ?: "(Unknown.kt:?)-unknownMethod()"
    }


    fun getClassNameMethodNameAndLineNumberPublic(): String {
        return classNameMethodNameAndLineNumber()
    }

    /**
     * Write to LogCat only, prepend class, method, and line number to message.
     * @param priority
     * @param tag
     * @param msg
     */
    override fun log(priority: Int, tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            val cml = classNameMethodNameAndLineNumber()
            val message = msg ?: "Message was null"
            when (priority) {
                Log.VERBOSE -> Log.v(tag, String.format("%s %s", cml, message))
                Log.DEBUG -> Log.d(tag, String.format("%s %s", cml, message))
                Log.INFO -> Log.i(tag, String.format("%s %s", cml, message))
                Log.WARN -> Log.w(tag, String.format("%s %s", cml, message))
                Log.ERROR -> Log.e(tag, String.format("%s %s", cml, message))
                else -> Log.e(tag, String.format("[This is called incorrectly] %s %s", cml, message))
            }
        }
    }

    /**
     * Write to LogCat only, prepend class, method, and line number to message.
     * @param priority
     * @param tag
     * @param msg
     */
    override fun log(priority: Int, tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG) {
            val cml = classNameMethodNameAndLineNumber()
            when (priority) {
                Log.VERBOSE -> Log.v(tag, String.format("%s %s", cml, msg), t)
                Log.DEBUG -> Log.d(tag, String.format("%s %s", cml, msg), t)
                Log.INFO -> Log.i(tag, String.format("%s %s", cml, msg), t)
                Log.WARN -> Log.w(tag, String.format("%s %s", cml, msg), t)
                Log.ERROR -> Log.e(tag, String.format("%s %s", cml, msg), t)
                else -> Log.e(tag, String.format("[This is called incorrectly] %s %s", cml, msg), t)
            }
        }
    }

    /**
     * Write to LogCat and Crashlytics (on next run of app)
     */
//    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    override fun <T>logNetworkFailure(call: Call<T>, t: Throwable) {
        logAction(getClassNameMethodNameAndLineNumberPublic(), "requestUrl", call.request().toString())
//        FirebaseCrashlytics.getInstance().recordException(t)
        if (BuildConfig.DEBUG) {
            Log.e("App", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), t.message), t)
        }
    }

//    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    override fun logException(e: Exception) {
//        FirebaseCrashlytics.getInstance().recordException(e)
        if (BuildConfig.DEBUG) {
            Log.e("App", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), e.message), e)
        }
    }

//    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    override fun logException(t: Throwable) {
//        FirebaseCrashlytics.getInstance().recordException(t)
        if (BuildConfig.DEBUG) {
            Log.e("App", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), t.message), t)
        }
    }

//    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    override fun logException(message: String) {
        logException(Exception(message))
    }

    override fun dropBreadCrumb(category: String, action: String, label: String, value: String) {
        val breadCrumb = String.format("%s | %s | %s | %s", category, action, label, value)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    override fun logAction(category: String, action: String, label: String) {
        val breadCrumb = String.format("%s | %s | %s", category, action, label)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    override fun logAction(category: String, action: String, label: String, value: String) {
        val breadCrumb = String.format("%s | %s | %s | %s", category, action, label, value)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    override fun logNetworkResponseFromBaseClass(category: String, action: String, label: String, value: String) {
        val breadCrumb = "$category | ${classNameMethodNameAndLineNumber()} | $action | $label | $value"
        if (BuildConfig.DEBUG) { log(Log.INFO, "BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    override fun safeAssertShouldNotReachHere(reason: String) {
        val assertionError = AssertionError(reason)
        if (BuildConfig.DEBUG) {
            logException(assertionError)
            throw assertionError
        } else {
//            FirebaseCrashlytics.getInstance().recordException(assertionError)
        }
    }
}
