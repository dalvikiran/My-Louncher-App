package com.kiran.install_app_info.models

import android.graphics.drawable.Drawable

data class AppDetails(
    var packageName: String?,
    var appName: String?,
    var appIcon : Drawable?,
    var launchActivity: String?,
    var versionCode: Int?,
    var versionName: String?,
)
