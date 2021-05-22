package com.kiran.myapplauncher.view_models

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.kiran.install_app_info.InstalledAppDetails
import com.kiran.install_app_info.models.AppDetails
import com.kiran.launcherapp.utils.Resource
import kotlinx.coroutines.*
import java.lang.Exception


class AppsDetailsViewModel private constructor(application: Application) :
    AndroidViewModel(application) {
    private val context: Context
    lateinit var appsList : List<AppDetails>

    companion object {
        private var appsDetailsViewModel: AppsDetailsViewModel? = null
        @Synchronized
        fun getInstance(application: Application): AppsDetailsViewModel? {
            if (appsDetailsViewModel == null) {
                appsDetailsViewModel = AppsDetailsViewModel(application)
                return appsDetailsViewModel
            }
            return appsDetailsViewModel
        }
    }

    init {
        context = application
    }


    fun getAppDetailsFromDevice() = liveData(Dispatchers.Default)  {
        emit(Resource.loading(data = null))
        try {
            emit( Resource.success(data = InstalledAppDetails.getAppDetails(context)) )
        }catch (e: Exception){
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }

    }

    fun findAppPosition(packageName : String) : Int{

        appsList!!.forEachIndexed { index, appDetails ->
            if (appDetails.packageName == packageName) {
                return index
            }
        }
        return  -1
    }

}
