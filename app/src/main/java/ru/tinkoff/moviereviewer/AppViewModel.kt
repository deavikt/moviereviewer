package ru.tinkoff.moviereviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    private val network = AppNetwork()
    private val movieAPI = network.getRetrofit().create(MovieAPI::class.java)
    private var favouriteMovieList: MutableList<MovieList.Movie> = mutableListOf()

    fun getPopularMovieList(): MutableLiveData<MovieList> {
        val liveDataMovieList: MutableLiveData<MovieList> = MutableLiveData()

        CoroutineScope(Dispatchers.Main).launch {
            liveDataMovieList.value = movieAPI.getPopularMovieList()
        }

        return liveDataMovieList
    }

    fun getMovieDescriptionById(kinopoiskId: Int): MutableLiveData<MovieById> {
        val liveDataMovieById: MutableLiveData<MovieById> = MutableLiveData()

        CoroutineScope(Dispatchers.Main).launch {
            liveDataMovieById.value = movieAPI.getMovieById(kinopoiskId)
        }

        return liveDataMovieById
    }

    fun getFavouriteMovieList(): MutableLiveData<MutableList<MovieList.Movie>> {
        val liveDataMovieList: MutableLiveData<MutableList<MovieList.Movie>> = MutableLiveData()

        CoroutineScope(Dispatchers.Main).launch {
            liveDataMovieList.value = favouriteMovieList
        }

        return liveDataMovieList
    }

    fun addMovieToFavourites(movie: MovieList.Movie) { favouriteMovieList.add(movie) }

    fun removeMovieFromFavourites(kinopoiskId: Int) {
        var movieIndex = 0

        for (index in 0 until favouriteMovieList.size) {
            if (favouriteMovieList[index].kinopoiskId == kinopoiskId)
                movieIndex = index
        }

        favouriteMovieList.removeAt(movieIndex)
    }
}