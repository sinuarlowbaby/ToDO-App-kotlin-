package com.sinuarlowbaby.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Common Colors
val FabBlue = Color(0xFF2D9CDB)
val LightGrayBorder = Color(0xFFE0E0E0)
val LabelGray = Color(0xFF828282)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    taskId: Int?, // If null, we are adding. If not null, we are editing.
    onBackClick: () -> Unit
) {
    val isDark by viewModel.isDarkTheme.collectAsState()

    val backgroundColor = if (isDark) DarkBackground else Color.White
    val titleColor = if (isDark) DarkTitle else Color.Black
    val labelColor = if (isDark) DarkSubtitle else LabelGray
    val fieldBorderColor = if (isDark) DarkSubtitle else LightGrayBorder
    val textColor = if (isDark) DarkTitle else Color.Black


    var taskText by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("Personal") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isLoaded by remember { mutableStateOf(false) }

    // Load data if editing
    LaunchedEffect(taskId) {
        if (taskId != null && taskId != -1) {
            val todo = viewModel.getTodoById(taskId)
            todo?.let {
                taskText = it.title
                selectedLabel = it.label
                sliderPosition = it.priority.toFloat()
            }
        }
        isLoaded = true
    }

    var expanded by remember { mutableStateOf(false) }
    val labels = listOf("Personal", "Work", "Study", "Groceries", "Health")

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 24.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (taskId == null || taskId == -1) "Add Task" else "Edit Task",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = titleColor
                    )
                )
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.Close,
                        "Close",
                        modifier = Modifier.size(32.dp),
                        tint = titleColor
                    )
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    if (taskText.isNotBlank()) {
                        viewModel.saveTodo(
                            taskText,
                            selectedLabel,
                            sliderPosition.toInt(),
                            if (taskId == -1) null else taskId
                        )
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FabBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (taskId == null || taskId == -1) "Done" else "Update", fontSize = 18.sp)
            }
        }
    )
    { innerPadding ->
        if (isLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Task Input
                Text(
                    "To-do",
                    style = MaterialTheme.typography.bodyMedium.copy(color = labelColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = taskText,
                    onValueChange = { taskText = it },
                    placeholder = { Text("What needs to be done?", color = if (isDark) DarkSubtitle else Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FabBlue,
                        unfocusedBorderColor = fieldBorderColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = FabBlue
                    )
                )


                Spacer(modifier = Modifier.height(24.dp))

                // Label Input (Editable + Dropdown)

                Text(
                    "Label",
                    style = MaterialTheme.typography.bodyMedium.copy(color = labelColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedLabel,
                        onValueChange = { selectedLabel = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, null, tint = textColor)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FabBlue,
                            unfocusedBorderColor = fieldBorderColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            cursorColor = FabBlue
                        )
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(if (isDark) DarkCard else Color.White)
                    ) {
                        labels.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(label, color = textColor) },
                                onClick = { selectedLabel = label; expanded = false }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Priority Slider
                Text(
                    "Priority",
                    style = MaterialTheme.typography.bodyMedium.copy(color = labelColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..2f,
                    steps = 1,
                    colors = SliderDefaults.colors(
                        thumbColor = FabBlue,
                        activeTrackColor = FabBlue,
                        inactiveTrackColor = fieldBorderColor
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Low", color = labelColor)
                    Text("Medium", color = labelColor)
                    Text("High", color = labelColor)
                }

            }
        }
    }
}