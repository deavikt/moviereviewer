package ru.tinkoff.moviereviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.FragmentMovieDescriptionBinding

class MovieDescriptionFragment(private val tabName: String,
                               private val id: Int) : Fragment() {

    private lateinit var binding: FragmentMovieDescriptionBinding
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDescriptionBinding.inflate(inflater)

        if (tabName == "Популярные")
            getMovieById()
        else
            getMovieByIdFromDb()

        // отображение фрагмента со списком популярных фильмов при нажатии на кнопку "назад"
        binding.backArrowIcon.setOnClickListener {
            (requireContext() as FragmentActivity).supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (tabName == "Популярные")
            getMovieById()
        else
            getMovieByIdFromDb()
    }

    // создание строки с написанием жанров фильма через запятую
    @SuppressWarnings("unchecked")
    private fun <T> getGenresLine(genreList: List<T>): String {
        var genres = "Жанры: "

        for (index in genreList.indices) {
            val genre = if (tabName == "Популярные") {
                (genreList as List<Genre>)[index].genre
            } else
                (genreList as List<String>)[index]

            genres += if (index == genreList.size - 1)
                genre
            else
                "$genre, "
        }

        return genres
    }

    // создание строки с написанием стран через запятую
    private fun <T> getCountriesLine(countryList: List<T>): String {
        var countries = "Страны: "

        for (index in countryList.indices) {
            val country = if (tabName == "Популярные") {
                (countryList as List<Country>)[index].country
            } else
                (countryList as List<String>)[index]

            countries += if (index == countryList.size - 1)
                country
            else
                "$country, "
        }

        return countries
    }

    // загрузка постера фильма
    private fun <T> loadMoviePoster(poster: T) {
        binding.largeMoviePoster.load(poster) {
            placeholder(R.drawable.movie_poster_placeholder)
            error(R.drawable.movie_poster_error)
        }
    }

    private fun getMovieById() {
        binding.apply {
            appViewModel.getMovieById(
                id,
                movieDescriptionView,
                failedInternetConnectionView
            ).observe(requireActivity()) {
                lifecycleScope.launch {
                    movieName.text = it.nameRu
                    movieDescription.text = it.description
                    movieGenres.text = getGenresLine(it.genres)
                    movieCountries.text = getCountriesLine(it.countries)
                    loadMoviePoster(it.posterUrl)
                }
            }
        }
    }

    private fun getMovieByIdFromDb() {
        binding.apply {
            appViewModel.getMovieByIdFromDb(id).observe(requireActivity()) {
                lifecycleScope.launch {
                    movieName.text = it.name
                    movieDescription.text = it.description
                    movieGenres.text = getGenresLine(it.genres)
                    movieCountries.text = getCountriesLine(it.countries)
                    loadMoviePoster(it.largePoster)
                }
            }
        }
    }
}