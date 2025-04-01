package com.game.chase

import com.game.chase.core.util.log.LogWrapper
import retrofit2.Call

// For testing
class FakeLogWrapper : LogWrapper {

    val logCalls = mutableListOf<String>()
    val exceptions = mutableListOf<Throwable>()

    override fun log(priority: Int, tag: String, msg: String?) {
        logCalls.add("[$priority][$tag]: $msg")
    }

    override fun log(priority: Int, tag: String, msg: String, t: Throwable) {
        logCalls.add("[$priority][$tag]: $msg (${t.message})")
        exceptions.add(t)
    }

    override fun <T> logNetworkFailure(call: Call<T>, t: Throwable) {
        logCalls.add("NETWORK FAIL: ${call.request().url} - ${t.message}")
        exceptions.add(t)
    }

    override fun logException(e: Exception) {
        exceptions.add(e)
    }

    override fun logException(t: Throwable) {
        exceptions.add(t)
    }

    override fun logException(message: String) {
        exceptions.add(Exception(message))
    }

    override fun dropBreadCrumb(category: String, action: String, label: String, value: String) {
        logCalls.add("Breadcrumb: $category | $action | $label | $value")
    }

    override fun logAction(category: String, action: String, label: String) {
        logCalls.add("Action: $category | $action | $label")
    }

    override fun logAction(category: String, action: String, label: String, value: String) {
        logCalls.add("Action: $category | $action | $label | $value")
    }

    override fun logNetworkResponseFromBaseClass(category: String, action: String, label: String, value: String) {
        logCalls.add("Network Response: $category | $action | $label | $value")
    }

    override fun safeAssertShouldNotReachHere(reason: String) {
        throw AssertionError("Should not reach here: $reason")
    }
}
