package com.evision.Category

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.evision.Category.Adapter.AdapterCategory
import com.evision.Category.Pojo.CategoryData
import com.evision.R
import com.evision.Utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {
    private lateinit var loader: AppDialog
    lateinit  var datacat: CategoryData
    lateinit var adapter: AdapterCategory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        loader= AppDialog(this)
        supportActionBar!!.title=resources.getString(R.string.menu_category)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Recv_cat.layoutManager= LinearLayoutManager(this)
        onHTTP().GETCALL(URL.GETCATEGORY,object : OnHttpResponse {
            override fun onSuccess(response: String) {
                loader.dismiss()
                datacat= Gson().fromJson(response,CategoryData::class.java)
                adapter=AdapterCategory(this@CategoryActivity,datacat)
                Recv_cat.adapter=adapter
                EvisionLog.D("## TAG-",response)
            }

            override fun onError(error: String) {
                EvisionLog.D("## TAG-",error)
            }

            override fun onStart() {
                loader.show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId)
        {
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
