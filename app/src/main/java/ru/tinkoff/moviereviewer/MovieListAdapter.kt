package ru.tinkoff.moviereviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.MovieItemBinding
import java.util.Locale

class MovieListAdapter<T>(private val tabName: String,
                          private val movieList: List<T>,
                          private val appViewModel: AppViewModel):
    RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    class ViewHolder(val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            if (tabName == "Популярные") {
                movieName.text = (movieList[position] as MovieList.Movie).nameRu
                movieGenreYear.text = getGenreYearLine(movieList[position] as MovieList.Movie)
                loadMoviePoster(smallMoviePoster, (movieList[position] as MovieList.Movie).posterUrlPreview)
            }
            else {
                movieName.text = (movieList[position] as Movie).name
                movieGenreYear.text = getGenreYearLine(movieList[position] as Movie)
                favouriteMovieIcon.visibility = View.VISIBLE
                loadMoviePoster(smallMoviePoster, (movieList[position] as Movie).smallPoster)
            }
        }

        val movieId = if (tabName == "Популярные")
            (movieList[position] as MovieList.Movie).kinopoiskId
        else
            (movieList[position] as Movie).id

        holder.itemView.apply {
            setOnClickListener {
                addMovieDescriptionFragment(context, movieId)
            }

            setOnLongClickListener {
                holder.binding.apply {
                    if (favouriteMovieIcon.visibility == View.INVISIBLE) {
                        addMovieToFavourites(
                            favouriteMovieIcon,
                            movieList[position] as MovieList.Movie,
                            context
                        )
                    }
                    else
                        deleteMovieFromFavourites(favouriteMovieIcon, movieId)
                }

                true
            }
        }
    }

    // создание строки с жанром и годом выпуска фильма
    private fun <T> getGenreYearLine(movie: T): String {

        var genreYear = if (tabName == "Популярные")
            "${(movie as MovieList.Movie).genres[0].genre} (${(movie as MovieList.Movie).year})"
        else
            "${(movie as Movie).genres[0]} (${(movie as Movie).year})"


        // Замена первой буквы в жанре фильма на заглавную
        genreYear = genreYear.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        return genreYear
    }

    // загрузка постера фильма
    private fun <T> loadMoviePoster(moviePosterView: ImageView, poster: T) {
        moviePosterView.load(poster) {
            transformations(RoundedCornersTransformation(5f))
            placeholder(R.drawable.movie_poster_placeholder)
            error(R.drawable.movie_poster_error)
        }
    }

    private suspend fun getBitmapMoviePoster(context: Context, posterUrl: String): Bitmap {
        val imageRequest = ImageRequest.Builder(context).data(posterUrl).build()
        val drawable = (ImageLoader(context).execute(imageRequest) as SuccessResult).drawable
        return (drawable as BitmapDrawable).bitmap
    }

    // замена фрагмента со списком популярных фильмов
    // на фрагмент с описанием выбранного фильма
    private fun addMovieDescriptionFragment(context: Context, id: Int) {
        (context as FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MovieDescriptionFragment(tabName, id))
            .addToBackStack("MovieDescriptionFragment")
            .commit()
    }

    private fun addMovieToFavourites(icon: ImageView,
                                     movie: MovieList.Movie,
                                     context: Context) {
        icon.visibility = View.VISIBLE

        appViewModel.getMovieDescription(movie.kinopoiskId).observe(context as FragmentActivity) {
            context.lifecycleScope.launch {
                val countryList: MutableList<String> = mutableListOf()
                val genreList: MutableList<String> = mutableListOf()

                for (country in movie.countries)
                    countryList.add(country.country)

                for (genre in movie.genres)
                    genreList.add(genre.genre)

                val favouriteMovie = Movie(
                    movie.kinopoiskId,
                    movie.nameRu,
                    countryList,
                    genreList,
                    movie.year,
                    it,
                    getBitmapMoviePoster(context, movie.posterUrlPreview),
                    getBitmapMoviePoster(context, movie.posterUrl)
                )

                appViewModel.addMovieToFavourites(favouriteMovie)
            }
        }
    }

    private fun deleteMovieFromFavourites(icon: ImageView, kinopoiskId: Int) {
        icon.visibility = View.INVISIBLE
        appViewModel.deleteMovieFromFavourites(kinopoiskId)
    }
}