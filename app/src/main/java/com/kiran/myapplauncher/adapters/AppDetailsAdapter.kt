package com.kiran.myapplauncher.adapters

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.RecyclerView;
import com.kiran.install_app_info.models.AppDetails
import com.kiran.myapplauncher.R
import com.kiran.myapplauncher.databinding.AppItemLayoutBinding


class AppDetailsAdapter(context: Context) :
    RecyclerView.Adapter<AppDetailsAdapter.AppDetailsViewHolder>(), Filterable {

    var context: Context
    private var appDetailsArrayList: ArrayList<AppDetails> = ArrayList()
    private var appDetailsFilterArrayList: ArrayList<AppDetails> = ArrayList()

    fun setAppList(appDetails: List<AppDetails>) {
        appDetailsArrayList.addAll(appDetails)
        appDetailsFilterArrayList.addAll(appDetails)
        notifyDataSetChanged()
    }

    fun addApp(app : AppDetails){
        appDetailsArrayList.add(app)
        appDetailsFilterArrayList.add(app)
        notifyItemInserted(appDetailsFilterArrayList.size-1)
    }

    fun removeApp(index: Int){
        val app = appDetailsArrayList[index]
        appDetailsArrayList.remove(app)
        appDetailsFilterArrayList.remove(app)
        notifyItemRemoved(index-1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppDetailsViewHolder {
        val appItemLayoutBinding: AppItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.app_item_layout, parent, false
        )
        return AppDetailsViewHolder(appItemLayoutBinding)
    }

    override fun onBindViewHolder(holderAppDetails: AppDetailsViewHolder, position: Int) {
        val appDetails = appDetailsFilterArrayList[position]
        if (appDetails != null) {
            holderAppDetails.appItemLayoutBinding.setAppDetails(appDetails)
        }
        holderAppDetails.appItemLayoutBinding.root.setOnClickListener {
            appDetails.packageName?.let { it1 -> launchApp(it1) }
        }
    }

    override fun getItemCount(): Int {
        return appDetailsFilterArrayList.size
    }

    inner class AppDetailsViewHolder(appItemLayoutBinding: AppItemLayoutBinding) :
        RecyclerView.ViewHolder(appItemLayoutBinding.getRoot()) {
        var appItemLayoutBinding: AppItemLayoutBinding

        init {
            this.appItemLayoutBinding = appItemLayoutBinding
        }
    }

    init {
        this.context = context
    }

    private fun launchApp(packageName : String){
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            context.startActivity(intent)
        }catch (e: Exception){

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    appDetailsFilterArrayList  = appDetailsArrayList as ArrayList<AppDetails>
                } else {
                    val resultList = ArrayList<AppDetails>()
                    for (row in appDetailsArrayList) {
                        if (row.appName?.toLowerCase()!!.contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    appDetailsFilterArrayList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = appDetailsFilterArrayList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                appDetailsFilterArrayList = results?.values as ArrayList<AppDetails>
                notifyDataSetChanged()
            }
        }
    }


}