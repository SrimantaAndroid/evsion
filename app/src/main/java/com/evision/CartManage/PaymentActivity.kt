package com.evision.CartManage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.braintreepayments.cardform.view.CardForm
import com.evision.CartManage.Adapter.AdapterCartCheckout
import com.evision.MyAccount.Pojo.StoreOrderPlaceResponse
import com.evision.R
import com.evision.Utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.sss.*
import org.json.JSONObject
import android.widget.PopupWindow
import com.evision.CartManage.Adapter.CardListAdapter
import com.evision.CartManage.Adapter.OnitemClick
import com.evision.CartManage.Pojo.*

class PaymentActivity : AppCompatActivity() {
    var DeliveryType = ""
    var DeliverySubType = ""
    var CUPONCODE = ""
    var CUPONPRICE = ""
    var DISCOUNTFOR=""
    var PRODUCT_IDS=""
    var is_same = false
    lateinit var loader: AppDialog
    lateinit var cardv: CardForm
    lateinit var customerAddress: CustomerAddress
    lateinit var customerAddress_billing: CustomerAddressBilling
    lateinit var adapterCart: AdapterCartCheckout
    lateinit var ordertotal: OrderTotal

    var cardtypelist= arrayOf("American express","uuuyuqyqeryqwury","whqudhoqwuyroi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sss)
        setSupportActionBar(toolbar)
        DeliverySubType = intent.getStringExtra("delivery_sub_type")
        DeliveryType = intent.getStringExtra("delivery_type")
        customerAddress = intent.getParcelableExtra("shipping")
        customerAddress_billing = intent.getParcelableExtra("billing")
        is_same = intent.getBooleanExtra("is_same", false)
        loader = AppDialog(this)
        toolbar.setTitle(R.string.title_add_toocart)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_back)
        RECV.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager
        cardv = findViewById(R.id.CARD_view) as CardForm

      //  val adapter = ArrayAdapter<String>(this,android.R.layout.select_dialog_item, cardtypelist)
        //tv_cardtype.setThreshold(1) //will start working from first character
       // tv_cardtype.setAdapter(adapter)
        tv_cardtype.setOnClickListener {
            openpopupdrop_downfoeweight()
        }

        cardv.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(false)

//                .postalCodeRequired(true)
//                .mobileNumberRequired(true)
//                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this)
        cardv.visibility = View.GONE
        RG_PAY.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.RB_CARD -> {
                    cardv.visibility = View.VISIBLE
                    llcardholdername.visibility=View.VISIBLE
                    cvvnumber.visibility=View.VISIBLE
                }
                R.id.RB_store -> {
                    cardv.visibility = View.GONE
                    llcardholdername.visibility=View.GONE
                    cvvnumber.visibility=View.GONE
                }
            }
        }


        loadData()
        RECV.layoutManager = LinearLayoutManager(this)

        EDX_apply.setOnClickListener {
            if (EDX_COUPON.text.toString().isEmpty()) {
                EDX_COUPON.setHintTextColor(Color.RED)
                return@setOnClickListener
            }

            UPDATECUPON(EDX_COUPON.text.toString())
        }

        BTN_pay.setOnClickListener {

               ORDERPLACE()
        }

    }

    private fun openpopupdrop_downfoeweight() {
        val popUpView =this.getLayoutInflater()?.inflate(R.layout.drop_down_layout, null)
        val  listPopupWindow = PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
                // ViewGroup.LayoutParams.WRAP_CONTENT, false
        )
        val reclyerview_dropdown: RecyclerView =popUpView!!.findViewById(R.id.reclyerview_dropdown)
        val cartlistadapter:CardListAdapter=CardListAdapter(cardtypelist,this,object :OnitemClick{
            override fun OnClickItem(position: Int) {
                super.OnClickItem(position)
                tv_cardtype.setText(cardtypelist.get(position))
                listPopupWindow.dismiss()
            }

        })
        reclyerview_dropdown?.layoutManager= LinearLayoutManager(this)
        reclyerview_dropdown?.adapter=cartlistadapter
        listPopupWindow?.showAsDropDown(tv_Card);

    }


    private fun ORDERPLACE() {
        if (cardv.visibility == View.VISIBLE) {
            if (cardv.cardEditText.text.isNullOrEmpty()) {
                cardv.cardEditText.requestFocus()
                return
            }

            if (cardv.expirationDateEditText.text.isNullOrEmpty()) {
                cardv.expirationDateEditText.requestFocus()
                return
            }
            if (cvvnumber.visibility==View.VISIBLE) {
                if (cvv.text.toString().equals("")){
                    cvv.requestFocus()
                    return
                }

            }
           /* if (cardv.cvvEditText.text.isNullOrEmpty()) {
                cardv.cvvEditText.requestFocus()
                return
            }*/
        }


        val params = HashMap<String, Any>()
        params.put("customer_id", ShareData(this).getUser()!!.customerId)
        params.put("first_name", customerAddress.fisrt_name)
        params.put("last_name", customerAddress.last_name)
        params.put("address_1", customerAddress.address)
        params.put("address_2", customerAddress.address)
        params.put("city_id", customerAddress.city_id)
        params.put("state_id", customerAddress.state_id)
        params.put("country_id", 169)
        params.put("telephone", customerAddress.telephone)
//        params.put("address_ship", if (is_same) 1 else 0)
        params.put("address_ship", 1)
//        if (is_same)
//        {
        params.put("shipping_address", customerAddress_billing.address)
        params.put("city_ship", customerAddress_billing.city_id)
        params.put("state_ship", customerAddress_billing.state_id)
        params.put("country_ship", customerAddress_billing.country_id)
        params.put("telephone_ship", customerAddress_billing.telephone)
//        }

        params.put("zone", if (DeliveryType.equals("delivery")) DeliverySubType.split(" ")[1] else "")
        params.put("delivery_cost", ordertotal.delivery)
        params.put("delivery_type", DeliveryType)
        params.put("pickup_id", if (DeliveryType.equals("delivery")) "1" else DeliverySubType)

        params.put("discount_for",DISCOUNTFOR)
        params.put("product_ids", PRODUCT_IDS)
        params.put("discount_amount",CUPONPRICE)
        EvisionLog.D("## URL-", URL.ORDERPALCEINSTORE)
        EvisionLog.D("## REQ PLACE-", Gson().toJson(params))
        onHTTP().POSTCALL(URL.ORDERPALCEINSTORE, params, object : OnHttpResponse {
            override fun onSuccess(response: String) {
                EvisionLog.D("## ORDER RESPONSE-", response)
                val data = Gson().fromJson(response, StoreOrderPlaceResponse::class.java)
                Toast.makeText(this@PaymentActivity, data.message, Toast.LENGTH_SHORT).show()
                if (data.code == 200) {
                    val orderID = JSONObject(response).optString("order_id")
                    if (cardv.visibility == View.VISIBLE) {
                        /**** TEMPORARY DATA *******/
                        var logindata = ShareData(this@PaymentActivity).getUser()
                        logindata!!.cartCount = 0
                        ShareData(this@PaymentActivity).SetUserData(Gson().toJson(logindata).toString())
                        /***** ***************/
                        val URL = URL.BASE + "checkout/payment.php?order_id=" + orderID + "&ccnumber="+cardv.cardNumber+"&ccexp="+cardv.expirationMonth+cardv.expirationYear+"&cvv="+cvv.text.toString()
                        startActivity(Intent(this@PaymentActivity, PaymentCeditCardActivity::class.java).putExtra("loaderURL", URL))
                        finish()
                    } else {
                        var logindata = ShareData(this@PaymentActivity).getUser()
                        logindata!!.cartCount = 0
                        ShareData(this@PaymentActivity).SetUserData(Gson().toJson(logindata).toString())
                        val inte = Intent(this@PaymentActivity, OrderSucessActivity::class.java).putExtra("response", response)
                        inte.putExtra("callurl", false)
                        inte.putExtra("data", response)
                        startActivity(inte)
                        finish()
                    }
                } else {
                }

                loader.dismiss()
            }

            override fun onError(error: String) {
                loader.dismiss()
            }

            override fun onStart() {
                if (!loader.isShowing)
                    loader.show()
            }

        })
    }

    private fun UPDATECUPON(coupon: String) {
        val params = HashMap<String, Any>()
        params.put("customer_id", ShareData(this).getUser()!!.customerId)
        params.put("coupon_code", coupon)
        onHTTP().POSTCALL(URL.UPDATECOUPN, params, object : OnHttpResponse {
            override fun onSuccess(response: String) {
                EvisionLog.D("## DATA-", response)
                if (JSONObject(response).has("message")) {
                    Toast.makeText(this@PaymentActivity, JSONObject(response).optString("message"), Toast.LENGTH_SHORT).show()
                    CUPONCODE=JSONObject(response).optString("coupon_code")
                    CUPONPRICE=JSONObject(response).optString("discount_amount")
                    DISCOUNTFOR=JSONObject(response).optString("discount_for")
                    PRODUCT_IDS=JSONObject(response).optString("proids")
                }
                val data = Gson().fromJson(response, CartResponse::class.java)

                loadData()
                loader.dismiss()
            }

            override fun onError(error: String) {
                loader.dismiss()
            }

            override fun onStart() {
                if (!loader.isShowing)
                     loader.show()
            }

        })


    }

    fun loadData() {
        val params = HashMap<String, Any>()
        params.put("customer_id", ShareData(this).getUser()!!.customerId)
        params.put("delivery_type", DeliveryType)
        params.put("coupon_code", CUPONCODE)
        params.put("discount_for",DISCOUNTFOR)
        params.put("product_ids",PRODUCT_IDS)
        params.put("coupon_amount", CUPONPRICE)
        onHTTP().POSTCALL(URL.GETREVIEW, params, object : OnHttpResponse {
            override fun onSuccess(response: String) {
                EvisionLog.D("## DATA DELIVERY-", response)
                val data = Gson().fromJson(response, CHECKOUTDATA::class.java)
                if (data.status == 400)
                    finish()
                else {
                   // if (RECV.adapter == null) {
                        adapterCart = AdapterCartCheckout(this@PaymentActivity, data.order_review, loader)
                        RECV.adapter = adapterCart
                   // }
                    adapterCart.notifyDataSetChanged()
                    ordertotal = data.order_totals[0]
//                    TXT_qtyno.setText("" + data.cart_count + "Qty")
                    subtotal.setText(data.order_totals[0].currency + data.order_totals[0].subtotal)
                    TACpercent.setText("" + data.order_totals[0].tax_name)
                    tax.setText(data.order_totals[0].currency + data.order_totals[0].tax)
                    TOTAL.setText(data.order_totals[0].currency + data.order_totals[0].grand_total)
                }
                loader.dismiss()
            }

            override fun onError(error: String) {
                loader.dismiss()
            }

            override fun onStart() {
                if (!loader.isShowing)
                    loader.show()
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
