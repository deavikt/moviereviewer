package ru.tinkoff.moviereviewer

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class Movie(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val countries: List<String>,
    @ColumnInfo val genres: List<String>,
    @ColumnInfo val year: String,
    @ColumnInfo val description: String,
    @ColumnInfo val smallPoster: Bitmap,
    @ColumnInfo val largePoster: Bitmap,
)