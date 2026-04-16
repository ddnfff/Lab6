package com.example.lab4

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lab4.ui.theme.Lab4Theme

class MainActivity : ComponentActivity() {
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab4Theme {
                val filterText by viewModel._filterText.collectAsState()
                PersonListScreen(
                    viewModel = viewModel,
                    filterText = filterText,
                    onFilterChange = { viewModel.setFilter(it) },
                    onPersonClick = { person ->
                        val intent = Intent(this, DetailActivity::class.java).apply {
                            putExtra("PERSON", person)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}


@Composable
fun PersonListScreen(
    viewModel: PersonViewModel,
    filterText: String,
    onFilterChange: (String) -> Unit,
    onPersonClick: (Person) -> Unit
) {
    val people by viewModel.filteredPeople.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Поле поиска
        TextField(
            value = filterText,
            onValueChange = onFilterChange,
            label = { Text("Поиск по имени") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(people) { person ->
                PersonItem(person, onClick = { onPersonClick(person) })
                Divider()
            }
        }

        Button(
            onClick = { viewModel.addPerson() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Добавить человека")
        }
    }
}

@Composable
fun PersonItem(person: Person, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = person.photoRes),
                contentDescription = "Фото ${person.name}",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = person.name, style = MaterialTheme.typography.titleLarge)
                Text(text = person.profession, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Возраст: ${person.age}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}