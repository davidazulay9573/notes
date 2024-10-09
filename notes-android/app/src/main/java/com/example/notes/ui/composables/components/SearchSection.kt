package com.example.notes.ui.composables.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchSection(
    onSearch: (query: String, searchBy: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchByOption by remember { mutableStateOf("title") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onSearch(searchQuery, searchByOption)
            },
            label = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = searchByOption == "title",
                onClick = {
                    searchByOption = "title"
                    onSearch(searchQuery, searchByOption)
                }
            )
            Text("Search by Title")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = searchByOption == "description",
                onClick = {
                    searchByOption = "description"
                    onSearch(searchQuery, searchByOption)
                }
            )
            Text("Search by Description")
        }
    }
}
