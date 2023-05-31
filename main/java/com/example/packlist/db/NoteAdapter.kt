package com.example.packlist.db

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.packlist.Fragments.NoteFragment
import com.example.packlist.R
import com.example.packlist.databinding.NoteListItemBinding
import com.example.packlist.entities.NoteItem
import com.example.packlist.utils.HtmlManager
import com.example.packlist.utils.TimeManager

class NoteAdapter(private val listener: Listener, private val defPref: SharedPreferences) : ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemComporator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)//Здесь мы создаем наш ViewHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position),listener,defPref)//Здесь мы заполняем нашу разметку
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Этот класс нужен для допустим у нас 50 элементов разметки
        // то у нас создается 50 таких классов и каждый
        // класс будет в себе в памяти хранить ссылку
        // на эту разметку и на все эти элементы потому что
        // нам нужно до них добираться чтоб прослушивать нажатия
        private val binding = NoteListItemBinding.bind(view)
        fun setData(note: NoteItem, listener:Listener ,defPref: SharedPreferences) = with(binding) {
            tvTitle.text = note.title //Эти три элемента из таблицы NoteItem
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time, defPref)

            itemView.setOnClickListener {//Слушатель нажатий на всю заметку при
                listener.onCLickItem(note)// нажатии на заметку она открывается
      }
       imDelete.setOnClickListener {//Слушатель нажатий на удаление заметок
           listener.deleteItem(note.id!!)
       }
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.note_list_item, parent, false)
                )

            }

        }
    }

    class ItemComporator : DiffUtil.ItemCallback<NoteItem>() {
        //класс DiffUtil.ItemCallback<NoteItem> он отвечает за то
        // допустим он будет сравнивать между собой элементы старого списка
        // и нового и он за нас уже будет делать
        // всю работу нам не нужно будет знать как же
        // обновить что обновлять какую функцию запустить для обновления
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem.id == newItem.id //это функция сравнивает если наши элементы отдельные равны тоесть похожи
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem == newItem //это функция сравнивает весь наш контент
        }

    }
interface Listener{

    fun deleteItem(id: Int)//Удаляет заметки
fun onCLickItem(note: NoteItem)//Редактирует заметки
}

}