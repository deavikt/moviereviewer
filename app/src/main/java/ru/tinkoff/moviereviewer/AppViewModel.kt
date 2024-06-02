package ru.tinkoff.moviereviewer

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppViewModel: ViewModel() {

    private val network = AppNetwork()
    private val movieAPI = network.getRetrofit().create(MovieAPI::class.java)
    private var favouriteMovieList: MutableList<MovieList.Movie> = mutableListOf()

    fun getPopularMovieList(movieListView: RecyclerView,
                            failedInternetConnectionView: LinearLayout): MutableLiveData<List<MovieList.Movie>> {
        val liveDataMovieList: MutableLiveData<List<MovieList.Movie>> = MutableLiveData()

        val call: Call<MovieList> = movieAPI.getPopularMovieList()
        
        call.enqueue(object : Callback<MovieList> {
            override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {

                if (response.body() != null) {
                    liveDataMovieList.value = response.body()!!.items

                    movieListView.visibility = View.VISIBLE
                    failedInternetConnectionView.visibility = View.INVISIBLE

                    Log.d("response", response.body()!!.items.toString())
                }
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {

                movieListView.visibility = View.INVISIBLE
                failedInternetConnectionView.visibility = View.VISIBLE

                Log.d("response", t.message.toString())
            }
        })

        return liveDataMovieList
    }

    fun getMovieDescriptionById(kinopoiskId: Int,
                                movieDescriptionView: ScrollView,
                                failedInternetConnectionView: LinearLayout): MutableLiveData<MovieById> {
        val liveDataMovieById: MutableLiveData<MovieById> = MutableLiveData()

        val call: Call<MovieById> = movieAPI.getMovieById(kinopoiskId)

        call.enqueue(object : Callback<MovieById> {
            override fun onResponse(call: Call<MovieById>, response: Response<MovieById>) {

                if (response.body() != null) {
                    liveDataMovieById.value = response.body()

                    movieDescriptionView.visibility = View.VISIBLE
                    failedInternetConnectionView.visibility = View.INVISIBLE

                    Log.d("response", response.body().toString())
                }
            }

            override fun onFailure(call: Call<MovieById>, t: Throwable) {

                movieDescriptionView.visibility = View.INVISIBLE
                failedInternetConnectionView.visibility = View.VISIBLE

                Log.d("response", t.message.toString())
            }
        })

        return liveDataMovieById
    }

    fun getFavouriteMovieList(): MutableLiveData<MutableList<MovieList.Movie>> {
        val liveDataMovieList: MutableLiveData<MutableList<MovieList.Movie>> = MutableLiveData()

        viewModelScope.launch {
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