package com.game.chase.core.util.log

import android.util.Log
import retrofit2.Call

/*
 * LogX is a wrapper around the Android Log class that provides a consistent interface for logging.
 * It also provides a way to log exceptions, actions, breadcrumbs, and network failures.
 *
 * Usage:
 *      Add LogX.init(AndroidLogWrapper()) into the onCreate() method of your Application class.
 */


object LogX {
    private lateinit var delegate: LogWrapper

    // Internal so only DI/setup can call it
    internal fun init(wrapper: LogWrapper) {
        delegate = wrapper
    }

    // Internal fallback to make testing easier
    internal fun overrideForTesting(wrapper: LogWrapper) {
        delegate = wrapper
    }

    // Expose delegate only to extension functions
    private val internalDelegate: LogWrapper
        get() = delegate

    /* Logging shorthand
 *
 * Usage:
 *      logX.d("My message")
 *      logX.e("My error message", Throwable())
 */
    fun v(msg: String, tag: String = "App") = internalDelegate.log(Log.VERBOSE, tag, msg)
    fun d(msg: String, tag: String = "App") = internalDelegate.log(Log.DEBUG, tag, msg)
    fun i(msg: String, tag: String = "App") = internalDelegate.log(Log.INFO, tag, msg)
    fun w(msg: String, tag: String = "App") = internalDelegate.log(Log.WARN, tag, msg)
    fun e(msg: String, tag: String = "App") = internalDelegate.log(Log.ERROR, tag, msg)
    fun e(msg: String, t: Throwable, tag: String = "App") = internalDelegate.log(Log.ERROR, tag, msg, t)

    /* Exceptions
     *
     * Usage:
     *      logX.ex(Throwable())
     *      logX.ex(Exception())
     *      logX.ex("My exception message")
     */
    fun ex(t: Throwable) = internalDelegate.logException(t)
    fun ex(e: Exception) = internalDelegate.logException(e)
    fun ex(message: String) = internalDelegate.logException(message)

    /* Actions
     *
     * Usage:
     *      logX.action("Category", "Action", "Label")
     *      logX.action("Category", "Action", "Label", "Value")
     */
    fun action(category: String, action: String, label: String) =
        internalDelegate.logAction(category, action, label)

    fun action(category: String, action: String, label: String, value: String) =
        internalDelegate.logAction(category, action, label, value)

    /* Breadcrumbs
     *
     * Usage:
     *      logX.breadcrumb("Category", "Action", "Label")
     *      logX.breadcrumb("Category", "Action", "Label", "Value")
     */
    fun breadcrumb(category: String, action: String, label: String, value: String) =
        internalDelegate.dropBreadCrumb(category, action, label, value)

    /* Network failure (wraps original call)
     *
     * Usage:
     *      logX.networkFail(call, Throwable())
     */
    fun <T> networkFail(call: Call<T>, t: Throwable) = internalDelegate.logNetworkFailure(call, t)

    /* Assertion
     *
     * Usage:
     *      log.xassert(false, "Assertion failed")
     */
    fun shouldNotReach(reason: String) = internalDelegate.safeAssertShouldNotReachHere(reason)
}

