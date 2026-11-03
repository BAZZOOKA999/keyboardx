package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClipboardItem
import com.example.data.model.PersonalWord
import com.example.data.model.QuickText

@Database(
    entities = [QuickText::class, PersonalWord::class, ClipboardItem::class],
    version = 1,
    exportSchema = false
)
abstract class KeyBoardXDatabase : RoomDatabase() {
    abstract fun quickTextDao(): QuickTextDao
    abstract fun personalWordDao(): PersonalWordDao
    abstract fun clipboardDao(): ClipboardDao

    companion object {
        @Volatile
        private var INSTANCE: KeyBoardXDatabase? = null

        fun getInstance(context: Context): KeyBoardXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyBoardXDatabase::class.java,
                    "keyboardx_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
