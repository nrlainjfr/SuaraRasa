    package com.example.SuaraRasa_A209008.data

    import androidx.compose.runtime.mutableStateListOf

    class EmergencyRepository {
        private val emergencyContacts = mutableStateListOf<EmergencyContact>()

        init {
            // Load default official contacts
            emergencyContacts.addAll(EmergencyContactsDataSource.getAllOfficialContacts())
        }

        fun getAllContacts(): List<EmergencyContact> = emergencyContacts

        fun get24x7Contacts(): List<EmergencyContact> =
            emergencyContacts.filter { it.is24Hours }

        fun getFavorites(): List<EmergencyContact> =
            emergencyContacts.filter { it.isFavorite }

        fun searchContacts(query: String): List<EmergencyContact> {
            return emergencyContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.location?.contains(query, ignoreCase = true) == true
            }
        }

        fun toggleFavorite(contactId: String) {
            emergencyContacts.replaceAll {
                if (it.id == contactId) it.copy(isFavorite = !it.isFavorite) else it
            }
        }

        fun addPersonalContact(contact: EmergencyContact) {
            emergencyContacts.add(contact)
        }
    }