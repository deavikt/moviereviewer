package ru.tinkoff.moviereviewer

data class MovieById(
    val nameRu: String,
    val description: String,
    val posterUrl: String,
    val genres: List<Genre>,
    val countries: List<Country>
)