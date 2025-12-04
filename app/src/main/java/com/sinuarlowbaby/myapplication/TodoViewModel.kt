package com.sinuarlowbaby.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. Define Sort Options
enum class SortOption {
    NEWEST_FIRST,
    OLDEST_FIRST,
    PRIORITY_HIGH,
    PRIORITY_LOW
}

class TodoViewModel(private val dao: TodoDao) : ViewModel() {

    // 2. Sort State
    private val _sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val currentSortOption: StateFlow<SortOption> = _sortOption

    // 3. Combine Database data + Sort Option
    val todoList: StateFlow<List<TodoItem>> = combine(
        dao.getAllTodos(),
        _sortOption
    ) { list, sortOrder ->
        when (sortOrder) {
            SortOption.NEWEST_FIRST -> list.sortedByDescending { it.date }
            SortOption.OLDEST_FIRST -> list.sortedBy { it.date }
            SortOption.PRIORITY_HIGH -> list.sortedByDescending { it.priority }
            SortOption.PRIORITY_LOW -> list.sortedBy { it.priority }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTodo(title: String, label: String, priority: Int) {
        val newItem = TodoItem(title = title, label = label, priority = priority)
        viewModelScope.launch { dao.upsertTodo(newItem) }
    }

    fun removeTodo(item: TodoItem) {
        viewModelScope.launch { dao.deleteTodo(item) }
    }

    fun toggleTodoCompletion(item: TodoItem) {
        viewModelScope.launch {
            dao.upsertTodo(item.copy(isDone = !item.isDone))
        }
    }

    fun onSortOptionChanged(option: SortOption) {
        _sortOption.value = option
    }
}