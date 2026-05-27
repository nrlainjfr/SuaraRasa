package com.example.SuaraRasa_A209008.data

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.SuaraRasa_A209008.R
import com.google.gson.annotations.SerializedName
import java.util.UUID
import kotlin.collections.map
import kotlin.let

@Immutable
data class MusicCategory(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("name") val name: String,
    @SerializedName("imageResId") val imageResId: Int = R.drawable.ic_default_song,
    @SerializedName("imageUriString") val imageUriString: String? = null,
    @SerializedName("songs") val songs: List<Song> = emptyList()
) {
    val imageUri: Uri?
        get() = imageUriString?.let { Uri.parse(it) }

    // Add this to help with serialization
    fun toSerializable(): MusicCategory {
        return copy(
            songs = songs.map { song ->
                song.copy(
                    audioUri = null, // We don't serialize the Uri directly
                    audioUriString = song.audioUri?.toString()
                )
            }
        )
    }

    companion object {
        fun fromSerialized(serialized: MusicCategory): MusicCategory {
            return serialized.copy(
                songs = serialized.songs.map { song ->
                    song.copy(
                        audioUri = song.audioUriString?.let { Uri.parse(it) }
                    )
                }
            )
        }
    }
}

@Immutable
data class Song(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("imageResId") val imageResId: Int,
    @SerializedName("audioResId") val audioResId: Int,
    @SerializedName("audioUri") val audioUri: Uri? = null,
    @SerializedName("audioUriString") val audioUriString: String? = null
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val is24Hours: Boolean,
    val availabilityHours: String = "",
    val description: String = "",
    val isFavorite: Boolean = false,
    val location: String = "Malaysia",
    val type: ContactType = ContactType.OFFICIAL
)

enum class ContactType {
    OFFICIAL, // Government/emergency services
    PERSONAL  // User-added contacts
}

data class FilterState(
    val selectedCategory: String = "All",
    val searchQuery: String = ""
)

