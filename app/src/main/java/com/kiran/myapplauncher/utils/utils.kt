package com.kiran.launcherapp.utils

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}


fun <T> List<T>.add(t:T): List<T>{
    return this + t
}