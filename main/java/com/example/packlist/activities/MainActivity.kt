package com.example.packlist.activities
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.packlist.Fragments.FragmentManager
import com.example.packlist.Fragments.NoteFragment
import com.example.packlist.Fragments.ShopListNamesFragment
import com.example.packlist.R
import com.example.packlist.billing.BillingManager
import com.example.packlist.databinding.ActivityMainBinding
import com.example.packlist.dialogs.NewListDialog
import com.example.packlist.settings.SettingsActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

class MainActivity : AppCompatActivity(),NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private var iAD: InterstitialAd? = null
    private lateinit var defPref:SharedPreferences
    private var currentMenuItemId = R.id.notes
    private var currentTheme = ""
    private var advertisingNumber = 0
    private var advertisingMax = 3
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = defPref.getString("theme_key","Blue").toString()
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
        setBottomNavListener()
            //Если мы купили удаление рекламы то с
        // помощью нижней строки она удаляется
        if (!pref.getBoolean(BillingManager.REMOVE_ADS_KEY,false))advertising()
    }
//Загружаем рекламу
    private fun advertising() {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.inter_ad_id), request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    iAD = ad//Загрузили успешно
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    iAD = null//Загрузили не успешно
                }
            })
    }

    private fun showInterAd(adListener: AdListener) {
        if (iAD != null && advertisingNumber > advertisingMax && !pref.getBoolean(BillingManager.REMOVE_ADS_KEY,false)){
            iAD?.fullScreenContentCallback = object : FullScreenContentCallback() {
                //Как только он закроет рекламу он может перейти в настройки
                override fun onAdDismissedFullScreenContent() {
                    iAD = null
                    advertising()
                    adListener.onFinish()
                }
                //Если произошла какая то ошибка
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    iAD = null
                    advertising()
                }
                //Запускается когда обновление полностью готова,
                // здесь можно установить таймер сколько будет показываться реклама
                override fun onAdShowedFullScreenContent() {
                    iAD = null
                    advertising()
                }
            }
            //Полностью уже запускает рекламу
            advertisingNumber = 0
            iAD?.show(this)
        } else {
            advertisingNumber++
            adListener.onFinish()

        }
    }

    private fun setBottomNavListener() {
        binding.BNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                //Если мы переходим в настройки то у нас выходит реклама
                R.id.settings -> {
                    showInterAd(object : AdListener {
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }

                    })

                }
                R.id.notes -> {
                    currentMenuItemId = R.id.notes
                    showInterAd(object : AdListener {
                        override fun onFinish() {
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity)
                        }

                    })
                }
                R.id.shop_list -> {
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)


                }
                R.id.new_item -> {
                    FragmentManager.currentFrag?.onClickNew()
                }

            }
            true
        }

    }

    override fun onResume() {
        super.onResume()
        binding.BNavigation.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key","Blue") !=currentTheme) recreate()
    }
    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key","Blue") == "Blue"){
            R.style.Theme_PackListBlue
        }else{
            R.style.Theme_PackListRed
        }
    }

    override fun onClick(name: String) {
        Log.d("Mylog","Name: $name")
    }
    interface AdListener{
        fun onFinish()
    }
}