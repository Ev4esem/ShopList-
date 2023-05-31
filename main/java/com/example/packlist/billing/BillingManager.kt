package com.example.packlist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.example.packlist.R

@Suppress("DEPRECATION")
class BillingManager(private val activity: AppCompatActivity) {
    private var bClient: BillingClient? = null


    init {
        setUpBillingClient()
    }
    //Настройка BillingClient с помощью него
    // мы сможем сделать подключение к Play маркету чтоб нам показывала
    // специальный диалог где пользователь уже может совершить покупку
    private fun setUpBillingClient() {
        bClient = BillingClient
            .newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases()
            .build()
    }
    //Сохраняем в память точто была произведена покупка
    private fun savePref(isPurchase:Boolean){
        val pref = activity.getSharedPreferences(MAIN_PREF,Context.MODE_PRIVATE)
        val editTore = pref.edit()
        editTore.putBoolean(REMOVE_ADS_KEY,isPurchase)
        editTore.apply()
    }

    fun startConnection(){
        bClient?.startConnection(object :BillingClientStateListener{
            //Сделали запрос но вдруг после его выключили или что то произошло
            override fun onBillingServiceDisconnected() {

            }
            //Все произошло успешно мы смогли связаться
            // с Play сервисом и мы смогли подключиться
            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }

        })
    }
        //Подготавливают нашу покупку для того что бы соединится с Play Store
    private fun getItem() {
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_ADD_ITEM)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
        bClient?.querySkuDetailsAsync(skuDetails.build()) { bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val bFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(list[0])
                                .build()
                        bClient?.launchBillingFlow(activity,bFlowParams)
                        }
                    }
                }
            }
        }
    }
    //Следит за покупками смотрит что там происходит
    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener { bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }

            }
        }
    }
    //Подтверждает покупку
    private fun nonConsumableItem(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                bClient?.acknowledgePurchase(acParams) {
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePref(true)
                        Toast.makeText(activity, R.string.shop_remove_ads,Toast.LENGTH_SHORT).show()
                    }else{
                        savePref(false)
                        Toast.makeText(activity, R.string.close_remove_ads,Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }
fun closeConnection(){
    bClient?.endConnection()
}


    companion object {
        const val REMOVE_ADD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}