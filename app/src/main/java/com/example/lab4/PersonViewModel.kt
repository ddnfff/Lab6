package com.example.lab4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map


enum class SortType {
    NAME_ASC,
    AGE_ASC,
    AGE_DESC
}

class PersonViewModel : ViewModel() {

    private val _people = MutableStateFlow(
        listOf(
            Person(
                1, "Ivan Ivanov", 28, "Разработчик",
                "Опыт работы 5 лет", R.drawable.avatar1, false
            ),
            Person(
                2, "Petr Petrov", 32, "Дизайнер",
                "Специалист по UI/UX", R.drawable.avatar2, false
            )
        )
    )
    val people: StateFlow<List<Person>> = _people.asStateFlow()

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    data class PersonStats(
        val count: Int = 0,
        val averageAge: Double = 0.0
    )

    private val _sortType = MutableStateFlow(SortType.NAME_ASC)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    val filteredPeople: StateFlow<List<Person>> =
        combine(_people, _filterText, _sortType) { list, filter, sortType ->
            val filteredList = if (filter.isBlank()) {
                list
            } else {
                list.filter { it.name.contains(filter, ignoreCase = true) }
            }

            when (sortType) {
                SortType.NAME_ASC -> filteredList.sortedBy { it.name.lowercase() }
                SortType.AGE_ASC -> filteredList.sortedBy { it.age }
                SortType.AGE_DESC -> filteredList.sortedByDescending { it.age }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _people.value
        )
    val stats: StateFlow<PersonStats> = filteredPeople
        .map { people ->
            if (people.isEmpty()) {
                PersonStats(0, 0.0)
            } else {
                PersonStats(
                    count = people.size,
                    averageAge = people.map { it.age }.average()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PersonStats()
        )
    fun setFilter(text: String) {
        _filterText.value = text
    }

    fun removePerson(personId: Int) {
        _people.value = _people.value.filter { it.id != personId }
    }

    fun setSortType(sortType: SortType) {
        _sortType.value = sortType
    }

    fun toggleFavorite(personId: Int) {
        _people.value = _people.value.map { person ->
            if (person.id == personId) {
                person.copy(isFavorite = !person.isFavorite)
            } else {
                person
            }
        }
    }

    fun addPerson() {
        val newId = (_people.value.maxOfOrNull { it.id } ?: 0) + 1
        _people.value = _people.value + Person(
            newId,
            "New Person $newId",
            25,
            "Профессия",
            "Описание",
            R.drawable.avatar_default,
            false
        )
    }
}