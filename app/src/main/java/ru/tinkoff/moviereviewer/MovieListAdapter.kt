package ru.tinkoff.moviereviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.tinkoff.moviereviewer.databinding.MovieItemBinding
import java.util.Locale

class MovieListAdapter(private val tabName: String,
                       private val movieList: List<MovieList.Movie>,
                       private val appViewModel: AppViewModel):
    RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    class ViewHolder(val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]

        holder.binding.movieName.text = movie.nameRu
        holder.binding.movieGenreYear.text = getGenreYearLine(movie)
        loadMoviePoster(holder, movie)

        if (tabName == "Избранное")
            holder.binding.favouriteMovieIcon.visibility = View.VISIBLE

        holder.itemView.setOnClickListener { addMovieDescriptionFragment(holder, position) }

        holder.itemView.setOnLongClickListener {
            if (holder.binding.favouriteMovieIcon.visibility == View.INVISIBLE)
                addMovieToFavourites(holder.binding.favouriteMovieIcon, movie)
            else
                removeMovieFromFavourites(holder.binding.favouriteMovieIcon, movie.kinopoiskId)

            true
        }
    }

    // создание строки с жанром и годом выпуска фильма
    private fun getGenreYearLine(popularFilm: MovieList.Movie): String {
        var genreYear = "${popularFilm.genres[0].genre} (${popularFilm.year})"

        // Замена первой буквы в жанре фильма на заглавную
        genreYear = genreYear.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        return genreYear
    }

    // загрузка постера фильма
    private fun loadMoviePoster(holder: ViewHolder, popularFilm: MovieList.Movie) {
        Glide
            .with(holder.itemView.context)
            .load(popularFilm.posterUrl)
            .placeholder(R.drawable.movie_poster_placeholder)
            .error(R.drawable.movie_poster_error)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(5))) // закругление краев постера
            .into(holder.binding.smallMoviePoster)
    }

    // замена фрагмента со списком популярных фильмов
    // на фрагмент с описанием выбранного фильма
    private fun addMovieDescriptionFragment(holder: ViewHolder, position: Int) {
        val movie = movieList[position]

        (holder.itemView.context as FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MovieDescriptionFragment(movie.kinopoiskId))
            .addToBackStack("MovieDescriptionFragment")
            .commit()
    }

    private fun addMovieToFavourites(icon: ImageView, movie: MovieList.Movie) {
        icon.visibility = View.VISIBLE
        appViewModel.addMovieToFavourites(movie)
    }

    private fun removeMovieFromFavourites(icon: ImageView, kinopoiskId: Int) {
        icon.visibility = View.INVISIBLE
        appViewModel.removeMovieFromFavourites(kinopoiskId)
    }
}