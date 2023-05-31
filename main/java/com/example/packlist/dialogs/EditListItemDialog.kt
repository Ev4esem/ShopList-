package com.example.packlist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.packlist.R
import com.example.packlist.databinding.EditListItemDialogBinding
import com.example.packlist.databinding.NewListDialogBinding
import com.example.packlist.entities.ShopListItem

object EditListItemDialog {
    fun showDialog(context: Context,item: ShopListItem, listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name)
            edInfo.setText(item.ItemInfo)
            if (item.itemType == 1) edInfo.visibility = View.GONE
            bUpdate.setOnClickListener {
                if (edName.text.toString().isNotEmpty()) {
                    listener.onClick(item.copy
                    (name = edName.text.toString(), ItemInfo = edInfo.text.toString()))
                }
                dialog?.dismiss()
        }
        }
        dialog = builder.create()
    dialog.window?.setBackgroundDrawable(null)
    dialog.show()
}

    interface Listener {
        fun onClick(item: ShopListItem)
    }
}


