package com.sinuarlowbaby.myapplication

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // Used for the Main List (can be filtered/sorted in ViewModel)
    @Query("SELECT * FROM todo_table")
    fun getAllTodos(): Flow<List<TodoItem>>

    // Used for Search Feature
    @Query("SELECT * FROM todo_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getTodos(searchQuery: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_table WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoItem?

    @Upsert // Handles both Insert and Update if ID exists
    suspend fun upsertTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)
}