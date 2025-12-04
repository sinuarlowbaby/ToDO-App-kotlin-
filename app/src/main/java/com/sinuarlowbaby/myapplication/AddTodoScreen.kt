package com.sinuarlowbaby.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun AddTodoScreen(onBackClick: () -> Unit, onSave: (String, String, Int) -> Unit) {
    var taskText by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf("Personal") }
    val labels = listOf("Personal", "Work", "Study")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp, start = 24.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Add Task", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp))
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.Close, "Close", modifier = Modifier.size(32.dp), tint = Color.Black)
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { if (taskText.isNotBlank()) onSave(taskText, selectedLabel, sliderPosition.toInt()) },
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FabBlue),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Done", fontSize = 18.sp) }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(30.dp))
            Text("To-do", style = MaterialTheme.typography.bodyMedium.copy(color = LabelGray), modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = taskText,
                onValueChange = { taskText = it },
                placeholder = { Text("What needs to be done?", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabBlue, unfocusedBorderColor = LightGrayBorder)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("Label", style = MaterialTheme.typography.bodyMedium.copy(color = LabelGray), modifier = Modifier.padding(bottom = 8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FabBlue, unfocusedBorderColor = LightGrayBorder)
                )
                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
                    labels.forEach { label ->
                        DropdownMenuItem(text = { Text(label) }, onClick = { selectedLabel = label; expanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Priority", style = MaterialTheme.typography.bodyMedium.copy(color = LabelGray), modifier = Modifier.padding(bottom = 8.dp))
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..2f,
                steps = 1,
                colors = SliderDefaults.colors(thumbColor = FabBlue, activeTrackColor = FabBlue, inactiveTrackColor = LightGrayBorder)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Low", color = LabelGray); Text("Medium", color = LabelGray); Text("High", color = LabelGray)
            }
        }
    }
}