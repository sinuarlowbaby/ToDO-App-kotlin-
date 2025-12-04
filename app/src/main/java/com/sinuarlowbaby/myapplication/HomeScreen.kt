package com.sinuarlowbaby.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List // <--- IMPORTANT IMPORT
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
import com.sinuarlowbaby.myapplication.ui.theme.*

// Reusing colors from AddTodoScreen if they are in the same package
// If FabBlue/TitleColor show red, ensure they are defined in AddTodoScreen.kt or Theme.kt

@Composable
fun HomeScreen(
    todoList: List<TodoItem>,
    currentSort: SortOption,        // <--- NEW PARAMETER
    onSortSelected: (SortOption) -> Unit, // <--- NEW PARAMETER
    onFabClick: () -> Unit,
    onToggle: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    // State for the dropdown menu visibility
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundColor, // Ensure this is defined or use Color(0xFFF5F5F5)
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = FabBlue, // Ensure FabBlue is visible from AddTodoScreen
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

            // --- HEADER ROW WITH SORT BUTTON ---
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
                        color = TitleColor // Ensure this is defined or use Color.Black
                    )
                )

                // SORT BUTTON & MENU
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
                        // Loop through all Sort Options
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
                                // Highlight the currently selected option
                                colors = MenuDefaults.itemColors(
                                    textColor = if(currentSort == option) FabBlue else Color.Black
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (todoList.isEmpty()) {
                // EMPTY STATE
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ensure you have R.drawable.no_data or remove/change this
                    Image(
                        painter = painterResource(id = R.drawable.no_data),
                        contentDescription = "Empty",
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Todos you add will appear here",
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
            modifier = Modifier
                .padding(12.dp)
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
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isDone) Color.Gray else Color.Black,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                )

                val priorityText = when(item.priority) {
                    0 -> "Low Priority"
                    1 -> "Medium Priority"
                    else -> "High Priority"
                }

                Text(
                    text = priorityText,
                    fontSize = 12.sp,
                    color = if (item.isDone) Color.LightGray else Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF5252)
                )
            }
        }
    }
}