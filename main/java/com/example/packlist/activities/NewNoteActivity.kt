package com.example.packlist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.packlist.Fragments.NoteFragment
import com.example.packlist.R
import com.example.packlist.databinding.ActivityNewNoteBinding
import com.example.packlist.entities.NoteItem
import com.example.packlist.utils.HtmlManager
import com.example.packlist.utils.MyTouchListener
import com.example.packlist.utils.TimeManager
import java.text.SimpleDateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences?=null
    private lateinit var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        setTextSize()
        onClickColorPicker()
   actionMenuCallback()
    }
private fun onClickColorPicker()= with(binding){
    ibBbxx.setOnClickListener { setColorForSelectedText(R.color.picker_bbxx) }
    ibRed.setOnClickListener { setColorForSelectedText(R.color.picker_red) }
    ibBlack.setOnClickListener { setColorForSelectedText(R.color.picker_black) }
    ibBrown.setOnClickListener { setColorForSelectedText(R.color.picker_brown) }
    ibBlue.setOnClickListener { setColorForSelectedText(R.color.picker_blue) }
    ibBluess.setOnClickListener { setColorForSelectedText(R.color.picker_bluess) }
    ibYellow.setOnClickListener { setColorForSelectedText(R.color.picker_yellow) }
    ibGreen.setOnClickListener { setColorForSelectedText(R.color.picker_green) }
    ibRedoss.setOnClickListener { setColorForSelectedText(R.color.picker_redoss) }
    ibFealetovie.setOnClickListener { setColorForSelectedText(R.color.picker_fealetovey) }
    ibGolyboy.setOnClickListener { setColorForSelectedText(R.color.picker_golyboy) }
    ibTeal700.setOnClickListener { setColorForSelectedText(R.color.teal_700) }
    ibOrange.setOnClickListener { setColorForSelectedText(R.color.picker_orange) }
    ibDown.setOnClickListener { setColorForSelectedText(R.color.picker_down)}
    idPink.setOnClickListener { setColorForSelectedText(R.color.picker_rozovie) }

}
    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getNote() {
        //Это функция отвечает за создание заметки или изменение заметки если она создана
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null) {
            note = sNote as NoteItem
            fillNote()
        }

    }

    // Если у нас note = null то эта функция не запускается
    // а если он не note != null то это функция запускается
    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title)
        edDesc.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    //Это функция добавляет menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Эта функция добавляет слушатели нажатий для наших кнопок в меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save) {//кнопка сохранить
            setMainResult()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.ic_bolt) {
            setBoldForSelectedText()
        } else if (item.itemId == R.id.ic_color) {
            if (binding.colorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Это функция отвечает за изменение шрифта текста
    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd
        val styles = edDesc.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            edDesc.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        edDesc.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }

    private fun setColorForSelectedText(colorId:Int) = with(binding) {
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd
        val styles = edDesc.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()) edDesc.text.removeSpan(styles[0])
        edDesc.text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor
                (this@NewNoteActivity,colorId)),
            startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }

    //Это функция выводит весь результат
    private fun setMainResult() {
        var editState = "new"
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)

        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDesc.text)
        )
    }

    //Это функция добавляет в нашу разметку данные
    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDesc.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    //Это функция добавляет формат времени


    //Это функция добавляет в Toolbar кнопку
    private fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    //Функция открывает наш  ColorPicker
    private fun openColorPicker() {
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

    //Функция закрывает наш ColorPicker
    private fun closeColorPicker() {
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : Animation.AnimationListener {
            //Когда запускается анимация
            override fun onAnimationStart(p0: Animation?) {

            }

            //Когда заканчивается анимация
            override fun onAnimationEnd(p0: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            //Если мы указали что она повторяется
            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
        binding.colorPicker.startAnimation(openAnim)
    }
    //Эта функция убирает кнопки копирование, вставка ,выделить все
private fun actionMenuCallback(){
    val actionCallback = object :ActionMode.Callback{
        override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            p1?.clear()
            return true//Это функция стирает меню
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            p1?.clear()
            return true//И это функция стирает меню
        }

        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            return true
        }

        override fun onDestroyActionMode(p0: ActionMode?) {

        }

    }

binding.edDesc.customSelectionActionModeCallback = actionCallback
}
    private fun setTextSize() = with(binding){
        Log.d("MyLog","size:${pref?.getString("title_size_key","16")}")
        edTitle.setTextSize(pref?.getString("title_size_key","18"))
        edDesc.setTextSize(pref?.getString("content_size_key","14"))
    }

 private fun EditText.setTextSize(size :String?){
     if (size !=null)this.textSize = size.toFloat()
 }
    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key","Blue") == "Blue"){
            R.style.Theme_PackListBlue
        }else{
            R.style.Theme_PackListRed
        }
    }
}