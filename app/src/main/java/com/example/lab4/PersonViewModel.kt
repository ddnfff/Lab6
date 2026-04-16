package com.example.lab4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PersonViewModel : ViewModel() {
    private val _people = MutableStateFlow(
        listOf(
            Person(1, "Ivan Ivanov", 28, "Разработчик",
                "Опыт работы 5 лет", R.drawable.avatar1),
            Person(2, "Petr Petrov", 32, "Дизайнер",
                "Специалист по UI/UX", R.drawable.avatar2)
        )
    )
    val _filterText = MutableStateFlow("")

    val people: StateFlow<List<Person>> = _people.asStateFlow()

    // это и есть тот самый `combine`, который должен работать
    val filteredPeople: StateFlow<List<Person>> = combine(_people, _filterText) { list, filter ->
        if (filter.isBlank()) list
        else list.filter { it.name.contains(filter, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setFilter(text: String) {
        _filterText.value = text
    }

    fun addPerson() {
        val newId = (_people.value.maxOfOrNull { it.id } ?: 0) + 1
        _people.value = _people.value + Person(
            newId, "Новый человек $newId", 25,
            "Профессия", "Описание", R.drawable.avatar_default
        )
    }
}