package com.game.chase.core.util.log

import android.util.Log
import retrofit2.Call

/* Logging shorthand
 *
 * Usage:
 *      logX.d("My message")
 *      logX.e("My error message", Throwable())
 */
fun LogX.v(msg: String, tag: String = "App") = log(Log.VERBOSE, tag, msg)
fun LogX.d(msg: String, tag: String = "App") = log(Log.DEBUG, tag, msg)
fun LogX.i(msg: String, tag: String = "App") = log(Log.INFO, tag, msg)
fun LogX.w(msg: String, tag: String = "App") = log(Log.WARN, tag, msg)
fun LogX.e(msg: String, tag: String = "App") = log(Log.ERROR, tag, msg)
fun LogX.e(msg: String, t: Throwable, tag: String = "App") = log(Log.ERROR, tag, msg, t)

/* Exceptions
 *
 * Usage:
 *      logX.ex(Throwable())
 *      logX.ex(Exception())
 *      logX.ex("My exception message")
 */
fun LogX.ex(t: Throwable) = logException(t)
fun LogX.ex(e: Exception) = logException(e)
fun LogX.ex(message: String) = logException(message)

/* Actions
 *
 * Usage:
 *      logX.action("Category", "Action", "Label")
 *      logX.action("Category", "Action", "Label", "Value")
 */
fun LogX.action(category: String, action: String, label: String) =
    logAction(category, action, label)

fun LogX.action(category: String, action: String, label: String, value: String) =
    logAction(category, action, label, value)

/* Breadcrumbs
 *
 * Usage:
 *      logX.breadcrumb("Category", "Action", "Label")
 *      logX.breadcrumb("Category", "Action", "Label", "Value")
 */
fun LogX.breadcrumb(category: String, action: String, label: String, value: String) =
    dropBreadCrumb(category, action, label, value)

/* Network failure (wraps original call)
 *
 * Usage:
 *      logX.networkFail(call, Throwable())
 */
fun <T> LogX.networkFail(call: Call<T>, t: Throwable) = logNetworkFailure(call, t)

/* Assertion
 *
 * Usage:
 *      log.xassert(false, "Assertion failed")
 */
fun LogX.shouldNotReach(reason: String) = safeAssertShouldNotReachHere(reason)