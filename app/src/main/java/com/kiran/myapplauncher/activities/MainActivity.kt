package com.kiran.myapplauncher.activities


import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kiran.install_app_info.InstalledAppDetails
import com.kiran.install_app_info.models.AppDetails
import com.kiran.launcherapp.utils.Status
import com.kiran.launcherapp.utils.ViewModelFactory
import com.kiran.myapplauncher.R
import com.kiran.myapplauncher.adapters.AppDetailsAdapter
import com.kiran.myapplauncher.databinding.ActivityMainBinding
import com.kiran.myapplauncher.view_models.AppsDetailsViewModel


class MainActivity : AppCompatActivity(), InstalledAppDetails.PackageReceiver {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appsDetailsViewModel: AppsDetailsViewModel
    private lateinit var appDetailsAdapter: AppDetailsAdapter

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val appList = InstalledAppDetails.getAppDetails(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mLayoutManager = LinearLayoutManager(this)
        binding.appsRecyclerView.layoutManager = mLayoutManager

        val viewModelFactory = ViewModelFactory(application)
        appsDetailsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AppsDetailsViewModel::class.java)

        binding.lifecycleOwner = this

        appDetailsAdapter = AppDetailsAdapter(this)
        binding.appsRecyclerView.adapter = appDetailsAdapter

        observeChanges()

        InstalledAppDetails.registerReceiver(this, this)
    }

    private fun observeChanges(){

        appsDetailsViewModel.getAppDetailsFromDevice().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.appsRecyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { apps -> retrieveApps(apps ) }
                    }
                    Status.ERROR -> {
                        binding.appsRecyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.appsRecyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun retrieveApps(apps: List<AppDetails>) {
        appsDetailsViewModel.appsList = apps
        appDetailsAdapter.apply {
            setAppList(apps)
            notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val item = menu?.findItem(R.id.action_search);
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
//                Log.d("onQueryTextChange", "query: " + query)
                appDetailsAdapter?.filter?.filter(query)
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                appDetailsAdapter?.filter?.filter("")
//                showToast("Action Collapse")
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
//                showToast("Action Expand")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        InstalledAppDetails.unRegisterReceiver(this)
        super.onDestroy()

    }

    override fun onPackageAdded(appDetails: AppDetails) {
        appDetails?.let {
            appDetailsAdapter.addApp(appDetails)
        }
    }

    override fun onPackageRemoved(packageName: String) {
        if (packageName != ""){
            val position = appsDetailsViewModel.findAppPosition(packageName)
            if (position >= 0){
//                appDetailsAdapter.removeApp(appsDetailsViewModel.appsList[position],position)
                appDetailsAdapter.removeApp(position)
            }
        }
    }
}