package com.sinuarlowbaby.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val label: String,
    val priority: Int, // 0 = Low, 1 = Medium, 2 = High
    val date: Long = System.currentTimeMillis(),
    val isDone: Boolean = false
)