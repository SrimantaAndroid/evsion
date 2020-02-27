package com.evision.Dashboard.Adapter

import android.content.Context
import android.graphics.ColorSpace
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.request.RequestOptions
import com.evision.Dashboard.Pojo.TopBanner
import com.evision.R

class AdapterTopBannerViewPager(mContext: Context, arrayList: List<TopBanner>) : PagerAdapter() {

    val mContext=mContext
    val arraylist=arrayList
    val inflater = LayoutInflater.from(mContext)
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
     return p0==p1    //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
       return arraylist.size//To change body of created functions use File | Settings | File Templates.
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layout = inflater.inflate(R.layout.top_banner, container, false)!!
        val ImageView=layout.findViewById<ImageView>(R.id.IMG_top)
        Glide.with(mContext).load(arraylist.get(position).banner_image).apply(RequestOptions().centerCrop().placeholder(R.drawable.ic_placeholder)).into(ImageView)
        container.addView(layout)

        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}