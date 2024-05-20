package ru.tinkoff.moviereviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    private val network = AppNetwork()
    private val popularMovieAPI = network.getRetrofit().create(PopularMovieAPI::class.java)

    fun getPopularMovieList(): MutableLiveData<MovieList> {
        val liveDataFilmList: MutableLiveData<MovieList> = MutableLiveData()

        CoroutineScope(Dispatchers.Main).launch {
            liveDataFilmList.value = popularMovieAPI.getPopularMovieList()
        }

        return liveDataFilmList
    }
}