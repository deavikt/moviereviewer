package ru.tinkoff.moviereviewer

import android.app.Application
import androidx.room.Room

class MovieReviewerApp: Application() {

    companion object {
        lateinit var movieDatabase: MovieDatabase
    }

    override fun onCreate() {
        super.onCreate()

        movieDatabase = Room.databaseBuilder(
            this,
            MovieDatabase::class.java,
            "movies"
        ).build()
    }
}