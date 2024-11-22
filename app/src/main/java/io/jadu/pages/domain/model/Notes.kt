package io.jadu.pages.domain.model

import android.net.Uri
import androidx.compose.ui.graphics.Path
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var description: String? = null,
    var position: Int = 0,
    var imageUri: List<Uri>? = null,
    var drawingPaths: List<List<Pair<Path, PathProperties>>>? = null,
    var isPinned: Boolean = false,
    var date: Long = System.currentTimeMillis(),
    var color: String? = null
){
    fun doesMatchSearchQuery(searchText: String): Boolean {
        return title.contains(searchText, ignoreCase = true) || description?.contains(searchText, ignoreCase = true) == true
    }
}

class Converters {

    @TypeConverter
    fun fromImageUrisList(value: List<Uri>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value?.map { it.toString() }, type)
    }


    @TypeConverter
    fun toImageUrisList(value: String?): List<Uri>? {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val uriStrings: List<String>? = gson.fromJson(value, type)
        return uriStrings?.map { Uri.parse(it) }
    }

    @TypeConverter
    fun fromDrawingPathsList(value: List<List<Pair<Path, PathProperties>>>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<List<Pair<Path, PathProperties>>>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toDrawingPathsList(value: String?): List<List<Pair<Path, PathProperties>>>? {
        val gson = Gson()
        val type = object : TypeToken<List<List<Pair<Path, PathProperties>>>>() {}.type
        return gson.fromJson(value, type)
    }
}
