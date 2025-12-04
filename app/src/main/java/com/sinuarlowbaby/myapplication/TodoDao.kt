package com.sinuarlowbaby.myapplication

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_table")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Upsert
    suspend fun upsertTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)
}