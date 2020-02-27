package com.evision.Dashboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evision.Dashboard.Adapter.AdapterHotCategory
import com.evision.Dashboard.Adapter.AdapterTopBannerViewPager
import com.evision.Dashboard.Pojo.Dasboard

import com.evision.R
import com.evision.Search.SearchFragment
import com.evision.Utils.AppDialog
import com.pixelcan.inkpageindicator.InkPageIndicator
import kotlinx.android.synthetic.main.fragment_dashboard.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DashboardFragment : Fragment(),DasboardContract.View {
    override fun preparepage(Hotlist: Dasboard) {
        dialogloader.dismiss()
        val adapter = AdapterTopBannerViewPager(this.activity!!, Hotlist.top_banner)
        viewpager.adapter = adapter
        val inkPageIndicator = view!!.findViewById(R.id.indicator) as InkPageIndicator
        inkPageIndicator.setViewPager(viewpager)
        categoryadapter = AdapterHotCategory(Hotlist.hot_category, activity!!)
        RecV.adapter=categoryadapter
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     lateinit var categoryadapter: AdapterHotCategory
    private lateinit var presenter:DashboardPresenterImple
    lateinit var viewpager: ViewPager
    lateinit var dialogloader: AppDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogloader = AppDialog(activity!!)
        /************* TOP BANNER PART ************/
        viewpager = view.findViewById<ViewPager>(R.id.ViewPager)


        /************** END TOP BANNER PART *********/


        RecV.layoutManager=LinearLayoutManager(activity)
        dialogloader.show()
        presenter= DashboardPresenterImple(this,DashboardIntractorImple())
        presenter.call_http_dashboard(this)

        SearchTXT.setOnClickListener {

            val sera= SearchFragment.newInstance("","")
            sera.show(activity!!.supportFragmentManager,"")
        }



    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DashboardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
