package com.example.packlist.Fragments

import androidx.appcompat.app.AppCompatActivity
import com.example.packlist.R

object FragmentManager {
    var currentFrag: BaseFragment? = null//Это функция нужна для того если нам

    // нужно что то сделать с фрагментом
    // удалить его или изменить мы можем просто
    // обратиться вот так FragmentManager.currentFrag
    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
//эта функция нужна для того чтобы переключатся между фрагментами
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.place_holder, newFragment)
        transaction.commit()
        currentFrag = newFragment
    }
}