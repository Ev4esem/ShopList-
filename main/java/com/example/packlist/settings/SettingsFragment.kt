package com.example.packlist.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.packlist.R
import com.example.packlist.billing.BillingManager

class SettingsFragment : PreferenceFragmentCompat() {
   private lateinit var removeAdsPref: Preference
    private lateinit var bManager: BillingManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference,rootKey)
        init()
    }
    //Слушатель нажатий для кнопки удаления рекламы
    private fun init(){
        bManager = BillingManager(activity as AppCompatActivity)
        removeAdsPref = findPreference("remove_ads_key")!!
        removeAdsPref.setOnPreferenceClickListener {
            Log.d("Mylog","Advertising : ")
            bManager.startConnection()
            true
        }
    }

    override fun onDestroy() {
       bManager.closeConnection()
        super.onDestroy()
    }

}
