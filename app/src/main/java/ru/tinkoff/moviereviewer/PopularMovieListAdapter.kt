package ru.tinkoff.moviereviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.tinkoff.moviereviewer.databinding.MovieItemBinding
import java.util.Locale

class PopularMovieListAdapter(private val movieList: MovieList):
    RecyclerView.Adapter<PopularMovieListAdapter.ViewHolder>() {

    class ViewHolder(val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = movieList.items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val popularFilm = movieList.items[position]

        holder.binding.filmName.text = popularFilm.nameRu
        holder.binding.filmGenreYear.text = getGenreYearLine(popularFilm)
        loadMoviePoster(holder, popularFilm)
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
            .into(holder.binding.filmPosterSmall)
    }
}