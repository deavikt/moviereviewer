package ru.tinkoff.moviereviewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun listToJson(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun jsonToList(json: String): List<String> = Gson().fromJson(json, object: TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun bitmapToByteArray(moviePoster: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        moviePoster.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun byteArrayToBitmap(moviePoster: ByteArray): Bitmap =
        BitmapFactory.decodeByteArray(moviePoster, 0, moviePoster.size)
}