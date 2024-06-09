package ru.tinkoff.moviereviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
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

        val lifecycleOwner = holder.itemView.context as FragmentActivity

        holder.binding.apply {
            movieName.text = getMovieName(position)
            movieGenreYear.text = getGenreYearLine(position)
            loadMoviePoster(smallMoviePoster, getMoviePosterType(position))
            setUpFavouriteMovieIcon(getMovieId(position), lifecycleOwner, favouriteMovieIcon)
        }

        holder.itemView.apply {
            setOnClickListener {
                addMovieDescriptionFragment(context, getMovieId(position))
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
                        deleteMovieFromFavourites(favouriteMovieIcon, getMovieId(position))
                }

                true
            }
        }
    }

    private fun getMovieId(position: Int): Int {
        val movieId = if (tabName == "Популярные")
            (movieList[position] as MovieList.Movie).kinopoiskId
        else
            (movieList[position] as Movie).id

        return movieId
    }

    private fun getMovieName(position: Int): String {
        val movieName = if (tabName == "Популярные")
            (movieList[position] as MovieList.Movie).nameRu
        else
            (movieList[position] as Movie).name

        return movieName
    }

    // создание строки с жанром и годом выпуска фильма
    private fun getGenreYearLine(position: Int): String {

        var genreYear = if (tabName == "Популярные")
            "${(movieList[position] as MovieList.Movie).genres[0].genre} (${(movieList[position] as MovieList.Movie).year})"
        else
            "${(movieList[position] as Movie).genres[0]} (${(movieList[position] as Movie).year})"


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

    private fun getMoviePosterType(position: Int): Any {
        val moviePosterType: Any = if (tabName == "Популярные") {
            (movieList[position] as MovieList.Movie).posterUrlPreview
        }
        else
            (movieList[position] as Movie).smallPoster

        return moviePosterType
    }

    private fun setUpFavouriteMovieIcon(movieId: Int,
                                        lifecycleOwner: LifecycleOwner,
                                        favouriteMovieIcon: ImageView) {
        if (tabName == "Популярные")
            appViewModel.getMovieByIdFromDb(movieId).observe(lifecycleOwner) {
                if (it != null)
                    favouriteMovieIcon.visibility = View.VISIBLE
            }
        else
            favouriteMovieIcon.visibility = View.VISIBLE
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

    private suspend fun getBitmapMoviePoster(context: Context, posterUrl: String): Bitmap {
        val imageRequest = ImageRequest.Builder(context).data(posterUrl).build()
        val drawable = (ImageLoader(context).execute(imageRequest) as SuccessResult).drawable
        return (drawable as BitmapDrawable).bitmap
    }

    private fun deleteMovieFromFavourites(icon: ImageView, kinopoiskId: Int) {
        icon.visibility = View.INVISIBLE
        appViewModel.deleteMovieFromFavourites(kinopoiskId)
    }
}