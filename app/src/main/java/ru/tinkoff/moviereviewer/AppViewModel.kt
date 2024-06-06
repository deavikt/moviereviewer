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
    private val movieDao = MovieReviewerApp.movieDatabase.movieDao()

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

    fun getMovieById(kinopoiskId: Int,
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

    fun getMovieByIdFromDb(id: Int): MutableLiveData<Movie> {
        val liveDataMovie: MutableLiveData<Movie> = MutableLiveData()

        viewModelScope.launch { liveDataMovie.value = movieDao.getMovieById(id) }

        return liveDataMovie
    }

    fun getMovieDescription(kinopoiskId: Int): MutableLiveData<String> {
        val liveDataMovieDescription: MutableLiveData<String> = MutableLiveData()

        val call: Call<MovieById> = movieAPI.getMovieById(kinopoiskId)

        call.enqueue(object : Callback<MovieById> {
            override fun onResponse(call: Call<MovieById>, response: Response<MovieById>) {

                if (response.body() != null) {
                    liveDataMovieDescription.value = response.body()!!.description

                    Log.d("response", response.body()!!.description)
                }
            }

            override fun onFailure(call: Call<MovieById>, t: Throwable) {
                Log.d("response", t.message.toString())
            }
        })

        return liveDataMovieDescription
    }

    fun getFavouriteMovieList(): MutableLiveData<List<Movie>> {
        val liveDataMovieList: MutableLiveData<List<Movie>> = MutableLiveData()

        viewModelScope.launch {
            liveDataMovieList.value = movieDao.getMovieList()
        }

        return liveDataMovieList
    }

    fun addMovieToFavourites(movie: Movie) {
        viewModelScope.launch { movieDao.insertMovie(movie) }
    }

    fun deleteMovieFromFavourites(kinopoiskId: Int) {
        viewModelScope.launch { movieDao.deleteMovieById(kinopoiskId) }
    }
}