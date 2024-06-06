package ru.tinkoff.moviereviewer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Movie::class], version = 1)
@TypeConverters(Converter::class)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun movieDao(): MovieDao
}