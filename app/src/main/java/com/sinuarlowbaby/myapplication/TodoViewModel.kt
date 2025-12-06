package com.sinuarlowbaby.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    NEWEST_FIRST,
    OLDEST_FIRST,
    PRIORITY_HIGH,
    PRIORITY_LOW
}

class TodoViewModel(private val dao: TodoDao) : ViewModel() {

    // --- Theme State (shared by all screens) ---
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun restoreTodo(todo: TodoItem) = viewModelScope.launch {
        dao.upsertTodo(todo)
    }


    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // ... rest of your existing code


    // --- Search State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // --- Sort & Filter State ---
    private val _sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val currentSortOption: StateFlow<SortOption> = _sortOption

    private val _filterLabel = MutableStateFlow("All")
    val currentFilterLabel: StateFlow<String> = _filterLabel

    // --- Main List Logic (Combines Database + Search + Filter + Sort) ---
    val todoList: StateFlow<List<TodoItem>> = combine(
        dao.getAllTodos(),
        _searchQuery,
        _sortOption,
        _filterLabel
    ) { list, query, sortOrder, filterLabel ->

        // 1. Apply Search
        var processedList = if (query.isBlank()) list else list.filter {
            it.title.contains(query, ignoreCase = true)
        }

        // 2. Apply Filter
        if (filterLabel != "All") {
            processedList = processedList.filter { it.label == filterLabel }
        }

        // 3. Apply Sort
        when (sortOrder) {
            SortOption.NEWEST_FIRST -> processedList.sortedByDescending { it.date }
            SortOption.OLDEST_FIRST -> processedList.sortedBy { it.date }
            SortOption.PRIORITY_HIGH -> processedList.sortedByDescending { it.priority }
            SortOption.PRIORITY_LOW -> processedList.sortedBy { it.priority }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Actions ---

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortOptionChanged(option: SortOption) {
        _sortOption.value = option
    }

    fun onFilterChanged(label: String) {
        _filterLabel.value = label
    }

    fun saveTodo(title: String, label: String, priority: Int, id: Int? = null) {
        viewModelScope.launch {
            val todo = TodoItem(
                id = id ?: 0, // 0 triggers auto-generate for new items
                title = title,
                label = label,
                priority = priority
            )
            dao.upsertTodo(todo)
        }
    }

    fun deleteTodo(todo: TodoItem) = viewModelScope.launch {
        dao.deleteTodo(todo)
    }

    fun toggleTodoCompletion(todo: TodoItem) = viewModelScope.launch {
        dao.upsertTodo(todo.copy(isDone = !todo.isDone))
    }

    suspend fun getTodoById(id: Int): TodoItem? {
        return dao.getTodoById(id)
    }
}

// Factory for ViewModel
class TodoViewModelFactory(private val dao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}