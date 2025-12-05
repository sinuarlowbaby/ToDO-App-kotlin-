package com.sinuarlowbaby.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Removed theme import to prevent errors if you haven't created the theme file yet.

// --- MISSING COLORS DEFINED HERE TO PREVENT CRASH ---
val BackgroundColor = Color(0xFFF5F5F5) // Light Gray Background
val TitleColor = Color(0xFF121212)      // Almost Black
val SubtitleColor = Color(0xFF757575)   // Dark Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todoList: List<TodoItem>,
    currentSort: SortOption,
    currentFilter: String,
    onSortSelected: (SortOption) -> Unit,
    onFilterSelected: (String) -> Unit,
    onFabClick: () -> Unit,
    onToggle: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val filterOptions = listOf("All", "Personal", "Work", "Study")

    Scaffold(
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = FabBlue, // Defined in AddTodoScreen.kt, make sure they are in same package
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- HEADER ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = TitleColor
                    )
                )

                Box {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Sort",
                            modifier = Modifier.size(32.dp),
                            tint = TitleColor
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    val label = when(option) {
                                        SortOption.NEWEST_FIRST -> "Date: Newest"
                                        SortOption.OLDEST_FIRST -> "Date: Oldest"
                                        SortOption.PRIORITY_HIGH -> "Priority: High"
                                        SortOption.PRIORITY_LOW -> "Priority: Low"
                                    }
                                    Text(label)
                                },
                                onClick = {
                                    onSortSelected(option)
                                    sortMenuExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = if(currentSort == option) FabBlue else Color.Black
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FILTER CHIPS ROW ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { filter ->
                    val isSelected = currentFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSelected(filter) },
                        label = { Text(filter) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FabBlue.copy(alpha = 0.2f),
                            selectedLabelColor = FabBlue,
                            selectedLeadingIconColor = FabBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (todoList.isEmpty()) {
                // EMPTY STATE
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Make sure you have a drawable named "no_data" in res/drawable
                    // If not, delete this Image block to stop the crash
                    Image(
                        painter = painterResource(id = R.drawable.no_data),
                        contentDescription = "Empty",
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No tasks found",
                        style = MaterialTheme.typography.bodyLarge.copy(color = SubtitleColor, fontSize = 16.sp)
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                // LIST STATE
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(todoList, key = { it.id }) { item ->
                        TodoItemCard(
                            item = item,
                            onToggle = { onToggle(item) },
                            onDelete = { onDelete(item) }
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
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = FabBlue)
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isDone) Color.Gray else Color.Black,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                )

                // Show Priority AND Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val priorityText = when(item.priority) {
                        0 -> "Low"
                        1 -> "Medium"
                        else -> "High"
                    }
                    Text(
                        text = "$priorityText • ${item.label}",
                        fontSize = 12.sp,
                        color = if (item.isDone) Color.LightGray else Color.Gray
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF5252))
            }
        }
    }
}