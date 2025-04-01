package com.game.chase.core.util.log

import retrofit2.Call

interface LogWrapper {
    fun log(priority: Int, tag: String, msg: String? = "Message was null")
    fun log(priority: Int, tag: String, msg: String, t: Throwable)
    fun <T> logNetworkFailure(call: Call<T>, t: Throwable)
    fun logException(e: Exception)
    fun logException(t: Throwable)
    fun logException(message: String)
    fun dropBreadCrumb(category: String, action: String, label: String, value: String)
    fun logAction(category: String, action: String, label: String)
    fun logAction(category: String, action: String, label: String, value: String)
    fun logNetworkResponseFromBaseClass(category: String, action: String, label: String, value: String)
    fun safeAssertShouldNotReachHere(reason: String)
}