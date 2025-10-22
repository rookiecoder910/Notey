package com.example.notey.screens
import com.example.notey.roomdb.Note
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notey.viewmodel.NoteViewModel

@Composable
fun DisplayDialog(
    viewModel: NoteViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    // Scroll state for the content inside the dialog
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedColor by remember { mutableStateOf(Color(0xFFFBCDCF)) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Create New Note",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                // Wrap content in a Column with scrolling
                Column(modifier = Modifier.verticalScroll(scrollState)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(text = "Note Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Description Input Field
                    OutlinedTextField( // Used OutlinedTextField for modern look
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(text = "Write your Content here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp), // Provide ample space
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Color Picker (Horizontal Scroll)
                    Text(
                        text = "Select Background Color:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MyColorPicker(
                        selectedColor = selectedColor,
                        onColorSelected = { selectedColor = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newNote = Note(
                            id = 0,
                            title = title,
                            description = description,
                            color = selectedColor.toArgb(),


                        )
                        // Only insert if the title is not blank
                        if (title.isNotBlank()) {
                            viewModel.insert(newNote)
                            onDismiss() // Close dialog after successful save
                        }
                    },
                    // Disable button if title is empty
                    enabled = title.isNotBlank()
                ) {
                    Text(text = "Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) // Used TextButton for a cleaner look
                {
                    Text(text = "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp) // Use rounded shape for the dialog box itself
        )
    }
}