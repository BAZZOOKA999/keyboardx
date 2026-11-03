package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_texts")
data class QuickText(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val shortcut: String? = null, // e.g. "meuemail"
    val category: String = "Geral",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_words")
data class PersonalWord(
    @PrimaryKey
    val word: String,
    val frequency: Int = 1,
    val language: String = "pt_BR",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "clipboard_items")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
