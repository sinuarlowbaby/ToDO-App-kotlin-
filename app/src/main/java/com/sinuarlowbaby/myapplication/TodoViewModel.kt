package com.sinuarlowbaby.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    NEWEST_FIRST,
    OLDEST_FIRST,
    PRIORITY_HIGH,
    PRIORITY_LOW
}

class TodoViewModel(private val dao: TodoDao) : ViewModel() {

    // 1. Sort State
    private val _sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val currentSortOption: StateFlow<SortOption> = _sortOption

    // 2. Filter State (New) -> Default is "All"
    private val _filterLabel = MutableStateFlow("All")
    val currentFilterLabel: StateFlow<String> = _filterLabel

    // 3. Combine Database + Sort + Filter
    val todoList: StateFlow<List<TodoItem>> = combine(
        dao.getAllTodos(),
        _sortOption,
        _filterLabel // Add filter to the combination
    ) { list, sortOrder, filterLabel ->

        // Step A: Apply Filter
        val filteredList = if (filterLabel == "All") {
            list
        } else {
            list.filter { it.label == filterLabel }
        }

        // Step B: Apply Sort to the filtered list
        when (sortOrder) {
            SortOption.NEWEST_FIRST -> filteredList.sortedByDescending { it.date }
            SortOption.OLDEST_FIRST -> filteredList.sortedBy { it.date }
            SortOption.PRIORITY_HIGH -> filteredList.sortedByDescending { it.priority }
            SortOption.PRIORITY_LOW -> filteredList.sortedBy { it.priority }
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

    // New function to update filter
    fun onFilterChanged(label: String) {
        _filterLabel.value = label
    }
}