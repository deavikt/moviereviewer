package ru.tinkoff.moviereviewer

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface PopularMovieAPI {

    // получение списка 250 популярных фильмов
    @Headers("x-api-key: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
    @GET("collections?type=TOP_250_MOVIES")
    suspend fun getPopularMovieList(): MovieList

    // получение описания фильма по его id
    @Headers("x-api-key: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
    @GET("{id}")
    suspend fun getMovieById(@Path("id") kinopoiskId: Int): MovieById
}