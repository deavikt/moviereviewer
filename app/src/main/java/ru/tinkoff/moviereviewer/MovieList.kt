package ru.tinkoff.moviereviewer

data class MovieList(
    val items: List<Movie>
) {
    data class Movie(
        val kinopoiskId: Int,
        val nameRu: String,
        val countries: List<Country>,
        val genres: List<Genre>,
        val year: String,
        val posterUrl: String,
        val posterUrlPreview: String
    ) {
        data class Country(
            val country: String
        )

        data class Genre(
            val genre: String
        )
    }
}