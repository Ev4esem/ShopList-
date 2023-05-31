package com.example.packlist.db

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.packlist.R
import com.example.packlist.databinding.ListNameItemBinding
import com.example.packlist.databinding.ShopLibraryItemBinding
import com.example.packlist.databinding.ShopListItemBinding
import com.example.packlist.entities.LibraryItem
import com.example.packlist.entities.ShopListNameItem
import com.example.packlist.entities.ShopListItem

class ShopListItemAdapter(private val listener: Listener) :
        ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComporator()) {
    //Условие выхода подсказки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0)
            ItemHolder.createShopItem(parent)
        else
            ItemHolder.createLibraryItem(parent)
    }

    //Если мы до этого не добавляли такую покупку то мы его название добавляем в подсказки
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //Отвечает за функционал кнопок самой покупки
        fun setItemData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.ItemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                chBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                chBox.setOnClickListener {
                listener.onClickItem(shopListItem.copy(itemChecked = chBox.isChecked), CHECK_BOX)
                }
            imEdit.setOnClickListener {
                listener.onClickItem(shopListItem, EDIT)
            }
            imDelete.setOnClickListener {
                listener.onClickItem(shopListItem, DELETE_LIST_ITEM)
            }
            }
        }
        //Отвечает за функционал кнопок посказки
        @SuppressLint("SuspiciousIndentation")
        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopLibraryItemBinding.bind(view)
            binding.apply {
              tvName.text = shopListItem.name
                imEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, CLICK_LIBRARY_ITEM)
                }
            }
        }
            //Если мы ответили что мы купили покупку то она зачеркивается и меняет свой цвет
        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            binding.apply {
                if (chBox.isChecked) {
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.grey_tvInfo))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context,R.color.grey_tvInfo))

                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }
        }
        //Если в описание к покупке ничего нет то мы не показываем edInfo
        private fun infoVisibility(shopListItem: ShopListItem): Int {
            return if (shopListItem.ItemInfo.isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object {
            //Разметка для покупок
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.shop_list_item, parent, false)
                )
            }
                //Разметка для Подсказок
            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.shop_library_item, parent, false)
                )
            }
        }
    }

    class ItemComporator : DiffUtil.ItemCallback<ShopListItem>() {
        override fun areItemsTheSame(
                oldItem: ShopListItem,
                newItem: ShopListItem,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
                oldItem: ShopListItem,
                newItem: ShopListItem,
        ): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun onClickItem(shopListName: ShopListItem,state:Int)

    }
    companion object{
        const val EDIT = 0//Для Изменения покупок
        const val CHECK_BOX = 1//Для пометки что мы купили что то из листа покупок
        const val EDIT_LIBRARY_ITEM = 2//Для изменения подсказок
        const val DELETE_LIBRARY_ITEM = 3//Для удаления подсказок
        const val CLICK_LIBRARY_ITEM = 4//Для того чтоб при нажатии на подсказку покупка появлялась
        const val DELETE_LIST_ITEM = 5//для удаления самих покупок
    }
        }




