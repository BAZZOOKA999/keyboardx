package com.example.data.local

import androidx.room.*
import com.example.data.model.ClipboardItem
import com.example.data.model.PersonalWord
import com.example.data.model.QuickText
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickTextDao {
    @Query("SELECT * FROM quick_texts ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllQuickTexts(): Flow<List<QuickText>>

    @Query("SELECT * FROM quick_texts WHERE category = :category ORDER BY isFavorite DESC, createdAt DESC")
    fun getQuickTextsByCategory(category: String): Flow<List<QuickText>>

    @Query("SELECT * FROM quick_texts WHERE shortcut = :shortcut LIMIT 1")
    suspend fun getByShortcut(shortcut: String): QuickText?

    @Query("SELECT * FROM quick_texts WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchQuickTexts(query: String): Flow<List<QuickText>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickText: QuickText): Long

    @Update
    suspend fun update(quickText: QuickText)

    @Delete
    suspend fun delete(quickText: QuickText)

    @Query("DELETE FROM quick_texts WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface PersonalWordDao {
    @Query("SELECT * FROM personal_words WHERE language = :language ORDER BY frequency DESC")
    fun getWords(language: String): Flow<List<PersonalWord>>

    @Query("SELECT * FROM personal_words WHERE word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT 10")
    suspend fun getMatchingWords(prefix: String): List<PersonalWord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(word: PersonalWord)

    @Query("DELETE FROM personal_words WHERE word = :word")
    suspend fun deleteWord(word: String)
}

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_items ORDER BY isPinned DESC, timestamp DESC LIMIT 50")
    fun getClipboardItems(): Flow<List<ClipboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClipboardItem): Long

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_items WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("UPDATE clipboard_items SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean)
}
