package com.example.SuaraRasa_A209008.ui.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.SuaraRasa_A209008.data.EmergencyContact
import com.example.SuaraRasa_A209008.data.EmergencyRepository
import com.example.SuaraRasa_A209008.data.FilterState

class EmergencyViewModel : ViewModel() {
    private val repository = EmergencyRepository()

    private val _filterState = mutableStateOf(FilterState())
    val filterState: State<FilterState> = _filterState

    private val _contacts = mutableStateOf<List<EmergencyContact>>(emptyList())
    val contacts: State<List<EmergencyContact>> = _contacts // ✅ Fix kat sini

    init {
        loadContacts()
    }

    fun loadContacts() {
        _contacts.value = when (_filterState.value.selectedCategory) {
            "24/7" -> repository.get24x7Contacts()
            "Favorites" -> repository.getFavorites()
            else -> repository.getAllContacts()
        }
    }

    fun updateFilter(category: String) {
        _filterState.value = _filterState.value.copy(selectedCategory = category)
        loadContacts()
    }

    fun searchContacts(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
        _contacts.value = if (query.isBlank()) {
            loadContacts()
            contacts.value
        } else {
            repository.searchContacts(query)
        }
    }

    fun toggleFavorite(contactId: String) {
        repository.toggleFavorite(contactId)
        loadContacts()
    }

    fun addPersonalContact(contact: EmergencyContact) {
        repository.addPersonalContact(contact)
        loadContacts()
    }
}
