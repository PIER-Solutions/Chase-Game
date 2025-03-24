package com.game.chase.core.util

import android.util.Log

import com.game.chase.BuildConfig
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call


object LogX {

    private const val STACK_TRACE_LEVELS_UP = 5

    /**
     * Get the current line number. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return int - Current line number.
     */
    private val lineNumber: Int
        get() = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].lineNumber

    /**
     * Get the current class name. Note, this will only work as called from this
     * class as it has to go a predetermined number of steps up the stack trace.
     * In this case 5.
     *
     * @return String - Current line number.
     */
    private // Removing ".java" and returning class name
    fun className(): String {
            val stackTraceLine = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP]
            //            val fileName = stackTraceLine.fileName
//            val split = fileName.split(".")
//            val splitLastLength = split.last().length + 1
//            var lastIndex = fileName.length - splitLastLength
//            if (lastIndex < 0) {
//                Log.e("BazLog", "fn = $fn, fileName = $fileName, splitLastLength = $splitLastLength, lastIndex = $lastIndex, stacktrace = ${stackTraceLine}, ")
//                lastIndex = 0
//            }
            // With proguard, the filename attribute was not working
            val st = stackTraceLine.toString()
                .substringBeforeLast('.')
                .substringBeforeLast('.')
                .substringBefore('$')
                .substringAfterLast('.') //fileName.substring(0, lastIndex)
            return st
        }

    /**
     * Get the current method name. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return String - Current line number.
     */
    private fun methodName(): String {
        return Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].methodName
    }


    /**
     * Returns the class name, method name, and line number from the currently
     * executing log call in the form <class_name>.<method_name>()-<line_number>
     *
     * @return String - String representing class name, method name, and line
     * number.
    </line_number></method_name></class_name> */
    private fun classNameMethodNameAndLineNumber(): String {
        return "[${className()}.${methodName()}()-$lineNumber]: "
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
    fun log(priority: Int, tag: String, msg: String? = "Message was null") {
        if (BuildConfig.DEBUG) {
            val cml = classNameMethodNameAndLineNumber()
            when (priority) {
                Log.VERBOSE -> Log.v(tag, String.format("%s %s", cml, msg))
                Log.DEBUG -> Log.d(tag, String.format("%s %s", cml, msg))
                Log.INFO -> Log.i(tag, String.format("%s %s", cml, msg))
                Log.WARN -> Log.w(tag, String.format("%s %s", cml, msg))
                Log.ERROR -> Log.e(tag, String.format("%s %s", cml, msg))
                else -> Log.e(tag, String.format("[This is called incorrectly] %s %s", cml, msg))
            }
        }
    }

    /**
     * Write to LogCat only, prepend class, method, and line number to message.
     * @param priority
     * @param tag
     * @param msg
     */
    fun log(priority: Int, tag: String, msg: String, t: Throwable) {
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
    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    inline fun <T>logNetworkFailure(call: Call<T>, t: Throwable) {
        logAction(getClassNameMethodNameAndLineNumberPublic(), "requestUrl", call.request().toString())
//        FirebaseCrashlytics.getInstance().recordException(t)
        if (BuildConfig.DEBUG) {
            Log.e("BaZing", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), t.message), t)
        }
    }

    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    inline fun logException(e: Exception) {
//        FirebaseCrashlytics.getInstance().recordException(e)
        if (BuildConfig.DEBUG) {
            Log.e("BaZing", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), e.message), e)
        }
    }

    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    inline fun logException(t: Throwable) {
//        FirebaseCrashlytics.getInstance().recordException(t)
        if (BuildConfig.DEBUG) {
            Log.e("BaZing", String.format("%s %s", getClassNameMethodNameAndLineNumberPublic(), t.message), t)
        }
    }

    @Suppress("NOTHING_TO_INLINE") // Inlined so Crashlytics reports each handled exception location as a different issue
    inline fun logException(message: String) {
        logException(Exception(message))
    }

    fun dropBreadCrumb(category: String, action: String, label: String, value: String) {
        val breadCrumb = String.format("%s | %s | %s | %s", category, action, label, value)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BaZing BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    fun logAction(category: String, action: String, label: String) {
        val breadCrumb = String.format("%s | %s | %s", category, action, label)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BaZing BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    fun logAction(category: String, action: String, label: String, value: String) {
        val breadCrumb = String.format("%s | %s | %s | %s", category, action, label, value)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BaZing BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    fun logNetworkResponseFromBaseClass(category: String, action: String, label: String, value: String) {
        val breadCrumb = String.format("%s | %s | %s | %s", category, "${className()}.$action.${methodName()}()-$lineNumber]: ", label, value)
        if (BuildConfig.DEBUG) { log(Log.INFO, "BaZing BreadCrumb", breadCrumb) }
//        FirebaseCrashlytics.getInstance().log(breadCrumb)
    }

    fun safeAssertShouldNotReachHere(reason: String) {
        val assertionError = AssertionError(reason)
        if (BuildConfig.DEBUG) {
            logException(assertionError)
            throw assertionError
        } else {
//            FirebaseCrashlytics.getInstance().recordException(assertionError)
        }
    }
}
