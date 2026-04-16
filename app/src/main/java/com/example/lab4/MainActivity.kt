package com.example.lab4
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lab4.ui.theme.Lab4Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.material.icons.outlined.Star
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab4Theme {
                val filterText by viewModel.filterText.collectAsState()
                val sortType by viewModel.sortType.collectAsState()

                PersonListScreen(
                    viewModel = viewModel,
                    filterText = filterText,
                    sortType = sortType,
                    onFilterChange = { viewModel.setFilter(it) },
                    onSortChange = { viewModel.setSortType(it) },
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
    sortType: SortType,
    onFilterChange: (String) -> Unit,
    onSortChange: (SortType) -> Unit,
    onPersonClick: (Person) -> Unit
) {
    val people by viewModel.filteredPeople.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Статистика",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Количество пользователей: ${stats.count}")
                Text("Средний возраст: %.1f".format(stats.averageAge))
            }
        }

        TextField(
            value = filterText,
            onValueChange = onFilterChange,
            label = { Text("Поиск по имени") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onSortChange(SortType.NAME_ASC) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (sortType == SortType.NAME_ASC) "А-Я ✓" else "А-Я",
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = { onSortChange(SortType.AGE_ASC) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (sortType == SortType.AGE_ASC) "Возраст↑ ✓" else "Возраст ↑",
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = { onSortChange(SortType.AGE_DESC) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (sortType == SortType.AGE_DESC) "Возраст↓ ✓" else "Возраст ↓",
                    fontSize = 12.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(people, key = { it.id }) { person ->
                PersonItem(
                    person = person,
                    onClick = { onPersonClick(person) },
                    onFavoriteClick = { viewModel.toggleFavorite(person.id) },
                    onDeleteClick = { viewModel.removePerson(person.id) }
                )
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
fun PersonItem(
    person: Person,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = person.photoRes),
                        contentDescription = "Фото ${person.name}",
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = person.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = person.profession,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Возраст: ${person.age}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (person.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (person.isFavorite) "Убрать из избранного" else "Добавить в избранное",
                            tint = if (person.isFavorite) Color(0xFFFFC107) else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            visible = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Удалить",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(visible) {
        if (!visible) {
            delay(300)
            onDeleteClick()
        }
    }
}