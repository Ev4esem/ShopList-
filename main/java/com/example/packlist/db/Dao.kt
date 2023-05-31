package com.example.packlist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.packlist.entities.LibraryItem
import com.example.packlist.entities.NoteItem
import com.example.packlist.entities.ShopListItem
import com.example.packlist.entities.ShopListNameItem
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Query( "SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>
    @Query( "SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Query( "SELECT * FROM shop_list_item WHERE listId LIKE:listId")
    fun getAllShopListItems(listId:Int): Flow<List<ShopListItem>>

    @Query( "SELECT * FROM library WHERE name LIKE:name")
    suspend fun getAllLibraryItems(name:String): List<LibraryItem>

    //Удаляет заметки
    @Query("DELETE  FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)
    //Удаляет списки
    @Query("DELETE  FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)
    @Query( "DELETE  FROM shop_list_item WHERE listId LIKE:listId")
    suspend fun deleteShopItemsByListId(listId:Int)
    @Query( "DELETE  FROM library WHERE id LIKE :id")
    suspend fun deleteShopLibraryItems(id: Int)
    @Query( "DELETE  FROM shop_list_item WHERE id LIKE :id")
    suspend fun deleteShopListItems(id: Int)
    @Insert
    suspend fun insertNote(note: NoteItem)
    @Insert
    suspend fun insertItem(shopListItem : ShopListItem)
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)
    @Insert
    suspend fun insertShopListName(name: ShopListNameItem)
    @Update
    suspend fun updateNote(note: NoteItem)
    @Update
    suspend fun updateLibraryItem(item: LibraryItem)
    //Это функция сохраняет состояние ChechBox
    @Update
    suspend fun updateListItem(item: ShopListItem)
    //Это функция для редоктирование списков
    @Update
    suspend fun updateListName(shopListName: ShopListNameItem)
}