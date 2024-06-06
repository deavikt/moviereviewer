package ru.tinkoff.moviereviewer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie")
    suspend fun getMovieList(): List<Movie>

    @Query("SELECT * FROM movie WHERE id = :id")
    suspend fun getMovieById(id: Int): Movie

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Query("DELETE FROM movie WHERE id = :id")
    suspend fun deleteMovieById(id: Int)
}