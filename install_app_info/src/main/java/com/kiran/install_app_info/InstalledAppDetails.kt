package com.kiran.install_app_info

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.util.Log
import com.kiran.install_app_info.models.AppDetails


class InstalledAppDetails {

    companion object{

        const val TAG = "AppDetails"
        var receiver : BroadcastReceiver? = null

        fun getAppDetails(context: Context) : List<AppDetails>{
            var appDetailList = ArrayList<AppDetails>()
            val apps: List<ApplicationInfo> = context.packageManager.getInstalledApplications(0)
            Log.e("App Count" , apps.size.toString())
            for (app in apps) {
//                if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0)
                    appDetailList.add(getApp(context, app))
            }
            val sortList = appDetailList.sortedWith(compareBy({it.appName}))
            return sortList
//            return appDetailList.sortedWith( compareBy({ it.appName}))
        }

        fun getApp(context :  Context, app : ApplicationInfo) : AppDetails{
            val packageInfo = context.packageManager.getPackageInfo(app.packageName, 0)
            app?.let {
//                Log.e("App Name" , context.packageManager.getApplicationLabel(app).toString())
                var activityName = ""

                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    `package` = app.packageName
                }
                val activityInfo = context.packageManager.queryIntentActivities(intent,0)
                if (activityInfo.size > 0){
                    activityName = activityInfo[0].activityInfo.name
                    activityName = activityName.substring(activityName.lastIndexOf(".")+1, activityName.length)
                }

                return AppDetails(
                        app.packageName,
                        context.packageManager.getApplicationLabel(app) as String,
                        app.loadIcon(context.packageManager),
                        activityName,
                        packageInfo.versionCode,
                        packageInfo.versionName
                )
            }
        }

        fun registerReceiver(context: Context, packageReceiver: PackageReceiver){
//        receiver = BroadcastReceiver()

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
//                    Log.e("PackageReceiver",intent.toString())
                    var packageName = ""
                    intent?.data?.let {
                        packageName = intent?.data.toString()
                        packageName = packageName.substring(packageName.indexOf(":")+1, packageName.length)
                    }
                    if (intent?.action ==  Intent.ACTION_PACKAGE_ADDED
                            || intent?.action ==  Intent.ACTION_PACKAGE_INSTALL){
                                val appInfo = context?.packageManager?.getApplicationInfo(packageName, 0)
                        packageReceiver.onPackageAdded(getApp(context!!, appInfo!!))
                    }else  if (intent?.action ==  Intent.ACTION_PACKAGE_REMOVED){
                        packageReceiver.onPackageRemoved(packageName)
                    }
                }
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
            intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
            intentFilter.addDataScheme("package")
            context.registerReceiver(receiver, intentFilter)
        }

        fun unRegisterReceiver(context : Context){
            receiver?.let {
                context.unregisterReceiver(receiver)
            }
        }
    }

    interface PackageReceiver{
        fun onPackageAdded(appDetails: AppDetails)
        fun onPackageRemoved(packageName: String)
    }

}