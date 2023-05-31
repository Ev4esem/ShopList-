package com.example.packlist.db

import androidx.lifecycle.*
import com.example.packlist.entities.LibraryItem
import com.example.packlist.entities.NoteItem
import com.example.packlist.entities.ShopListItem
import com.example.packlist.entities.ShopListNameItem
import kotlinx.coroutines.launch

class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    //С помощью database мы сможем получить интерфейс Dao
    val dao = dataBase.getDao()//Наш интерфейс
    val libraryItems = MutableLiveData <List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allShopListNames: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>> {
        return dao.getAllShopListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch {
         libraryItems.postValue(dao.getAllLibraryItems(name))
    }
    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)//Dao это и есть наша бизнес логика
    }

    fun insertShopListNames(listname: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listname)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertItem(shopListItem)
        if (!isLibraryItemExists(shopListItem.name)) dao.insertLibraryItem(
            LibraryItem(null, shopListItem.name)
        )
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }
    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }
    fun updateListItem(item: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(item)
    }

    //Это функция для редоктирования списков
    fun updateListName(shopListName: ShopListNameItem) = viewModelScope.launch {
        dao.updateListName(shopListName)
    }

    //Удаляет заметки
    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }
        //Удаление Подсказок в листе покупок
    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteShopLibraryItems(id)
    }
        //Удаление самого листа покупок
    fun deleteListItem(id: Int) = viewModelScope.launch {
        dao.deleteShopListItems(id)
    }
    //Удаляет списки
    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if (deleteList) dao.deleteShopListName(id)
        dao.deleteShopItemsByListId(id)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean {
        return dao.getAllLibraryItems(name).isNotEmpty()
    }

    class MainViewModelFactory(val database: MainDataBase) : ViewModelProvider.Factory {
        //Этот класс нужен для того чтобы иницилизировать class MainViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }


}