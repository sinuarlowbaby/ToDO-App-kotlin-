package com.sinuarlowbaby.myapplication

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Same color every time for a given category/label
fun categoryColorFor(label: String): Color = when (label) {
    "Personal"  -> Color(0xFF6366F1) // Indigo
    "Work"      -> Color(0xFFEC4899) // Pink
    "Study"     -> Color(0xFFF97316) // Orange
    "Groceries" -> Color(0xFF22C55E) // Green
    "Health"    -> Color(0xFF06B6D4) // Cyan
    else        -> Color(0xFF9CA3AF) // Gray fallback
}

fun priorityText(priority: Int): String = when (priority) {
    0 -> "Low"
    1 -> "Medium"
    else -> "High"
}

fun priorityColor(priority: Int): Color = when (priority) {
    0 -> Color(0xFF22C55E) // Green
    1 -> Color(0xFFF97316) // Orange
    else -> Color(0xFFEF4444) // Red
}

@Composable
fun rememberFormattedTime(timestamp: Long): String {
    return remember(timestamp) {
        val formatter = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
        formatter.format(Date(timestamp))
    }
}

// ---- LIGHT COLORS ----
val LightBackground = Color(0xFFF7F8FA)
val LightTitle = Color(0xFF0F172A)
val LightSubtitle = Color(0xFF64748B)
val LightCard = Color(0xFFFFFFFF)

// ---- DARK COLORS ----
val DarkBackground = Color(0xFF0B1220)
val DarkTitle = Color(0xFFE5E7EB)
val DarkSubtitle = Color(0xFF9CA3AF)
val DarkCard = Color(0xFF111827)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TodoViewModel,
    onAddClick: () -> Unit,
    onEditClick: (TodoItem) -> Unit
) {
    val todoList by viewModel.todoList.collectAsState()
    val searchText by viewModel.searchQuery.collectAsState()
    val currentSort by viewModel.currentSortOption.collectAsState()
    val currentFilter by viewModel.currentFilterLabel.collectAsState()

    val haptic = LocalHapticFeedback.current

    var sortMenuExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // --- FIX START: Use ViewModel state directly ---
    // Remove local 'remember' state. Listen to the VM.
    val isDark by viewModel.isDarkTheme.collectAsState()

    // Remove LaunchedEffect(Unit) { viewModel.setDarkTheme... }
    // because it resets the theme every time you navigate back here.
    // --- FIX END ---

    val themeRotation by animateFloatAsState(
        targetValue = if (isDark) 180f else 0f,
        label = "themeRotation"
    )

    val themeScale by animateFloatAsState(
        targetValue = if (isDark) 1.1f else 1f,
        label = "themeScale"
    )

    val snackbarHostState = remember { SnackbarHostState() }



    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val titleColor = if (isDark) DarkTitle else LightTitle
    val subtitleColor = if (isDark) DarkSubtitle else LightSubtitle
    val cardColor = if (isDark) DarkCard else LightCard

    // ---- STATUS BAR ICON COLOR FIX ----
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = !isDark
        // Set status bar color
        window.statusBarColor = backgroundColor.toArgb()
    }

    var recentlyDeletedTodo by remember { mutableStateOf<TodoItem?>(null) }
    val scope = rememberCoroutineScope()

    val filterOptions = listOf("All", "Personal", "Work", "Study", "Groceries", "Health")

    Scaffold(

        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = FabBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(30.dp))
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            // ---- HEADER ROW ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    )
                    Text(
                        text = "",
                        fontSize = 13.sp,
                        color = subtitleColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // 🔍 SEARCH ICON
                    IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = titleColor
                        )
                    }

                    // 🌙 THEME TOGGLE
                    IconButton(onClick = {
                        viewModel.toggleTheme()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)}
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme",
                            tint = titleColor,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = themeRotation   // 🔄 spin
                                scaleX = themeScale         // 🔍 small zoom
                                scaleY = themeScale
                            }
                        )
                    }

                    // ⬇ SORT ICON
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, "Sort", tint = titleColor)
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            modifier = Modifier.background(cardColor)
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.name.replace("_", " "),
                                            color = titleColor
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSortOptionChanged(option)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ---- EXPANDABLE SEARCH BAR (ABOVE FILTERS) ----
            AnimatedVisibility(visible = isSearchExpanded) {
                TextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    placeholder = { Text("Search your tasks...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedPlaceholderColor = subtitleColor,
                        unfocusedPlaceholderColor = subtitleColor,
                        focusedLeadingIconColor = subtitleColor,
                        unfocusedLeadingIconColor = subtitleColor,
                        cursorColor = FabBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---- FILTER CHIPS ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Categories (Filter Chips)
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = currentFilter == filter

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onFilterChanged(filter)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            label = {
                                Text(
                                    filter,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FabBlue.copy(alpha = 0.16f),
                                selectedLabelColor = FabBlue,
                                containerColor = Color.Transparent,
                                labelColor = subtitleColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ---- TODO LIST ----
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items = todoList, key = { it.id }) { item ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {

                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                recentlyDeletedTodo = item
                                viewModel.deleteTodo(item)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Task deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        recentlyDeletedTodo?.let { todo ->
                                            viewModel.restoreTodo(todo)
                                        }
                                    }
                                    // Clear the recently deleted item after snackbar is dismissed or action is taken
                                    recentlyDeletedTodo = null
                                }


                                true
                            } else false
                        }
                    )


                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFEF4444), RoundedCornerShape(16.dp))
                                    .padding(end = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = Color.White)
                            }
                        }
                    ) {
                        TodoItemCard(
                            item = item,
                            onToggle = {
                                viewModel.toggleTodoCompletion(item)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onClick = {
                                onEditClick(item)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDelete = {
                                viewModel.deleteTodo(item)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            cardColor = cardColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    item: TodoItem,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    titleColor: Color,
    subtitleColor: Color,
    cardColor: Color
) {
    val categoryColor = categoryColorFor(item.label)
    val priorityLabel = priorityText(item.priority)
    val createdAtText = rememberFormattedTime(item.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = FabBlue)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                // Title
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isDone) subtitleColor else titleColor,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom row: left = category, right = priority + time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // LEFT: category dot + label
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(categoryColor)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                    }

                    // RIGHT: priority + created time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = priorityLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = priorityColor(item.priority)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = createdAtText,
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                }
            }
        }
    }
}
