package ru.tinkoff.moviereviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.FragmentMovieDescriptionBinding

class MovieDescriptionFragment(private val kinopoiskId: Int) : Fragment() {

    private lateinit var binding: FragmentMovieDescriptionBinding
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDescriptionBinding.inflate(inflater)

        getMovieDescriptionById()

        // отображение фрагмента со списком популярных фильмов при нажатии на кнопку "назад"
        binding.backArrowIcon.setOnClickListener {
            (requireContext() as FragmentActivity).supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getMovieDescriptionById()
    }

    // создание строки с написанием жанров фильма через запятую
    private fun getGenresLine(genreList: List<MovieById.Genre>): String {
        var genres = "Жанры: "

        for (index in genreList.indices) {
            genres += if (index == genreList.size - 1)
                genreList[index].genre
            else
                genreList[index].genre + ", "
        }
        return genres
    }

    // создание строки с написанием стран через запятую
    private fun getCountriesLine(countryList: List<MovieById.Country>): String {
        var countries = "Страны: "

        for (index in countryList.indices) {
            countries += if (index == countryList.size - 1)
                countryList[index].country
            else
                countryList[index].country + ", "
        }

        return countries
    }

    // загрузка постера фильма
    private fun loadFilmPoster(posterUrl: String) {
        Glide
            .with(requireContext())
            .load(posterUrl)
            .placeholder(R.drawable.movie_poster_placeholder)
            .error(R.drawable.movie_poster_error)
            .into(binding.largeMoviePoster)
    }

    private fun getMovieDescriptionById() {
        appViewModel.getMovieDescriptionById(
            kinopoiskId,
            binding.movieDescriptionView,
            binding.failedInternetConnectionView
        ).observe(requireActivity()) {
            lifecycleScope.launch {
                binding.movieName2.text = it.nameRu
                binding.movieDescription.text = it.description
                binding.movieGenres.text = getGenresLine(it.genres)
                binding.movieCountries.text = getCountriesLine(it.countries)
                loadFilmPoster(it.posterUrl)
            }
        }
    }
}