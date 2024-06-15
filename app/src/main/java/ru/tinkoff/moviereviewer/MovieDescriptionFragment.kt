package ru.tinkoff.moviereviewer

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.FragmentMovieDescriptionBinding

class MovieDescriptionFragment: Fragment() {

    private lateinit var binding: FragmentMovieDescriptionBinding
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDescriptionBinding.inflate(inflater)

        binding.backArrowIcon.setOnClickListener { removeMovieDescriptionFragment() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (appViewModel.getMovieListType() == "Популярные")
            getMovieById()
        else
            getMovieByIdFromDb(appViewModel.getSelectedMovieId())

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeMovieDescriptionFragment()
        }
    }

    // создание строки с написанием жанров фильма через запятую
    @SuppressWarnings("unchecked")
    private fun <T> getGenresLine(genreList: List<T>): String {
        var genres = "Жанры: "

        for (index in genreList.indices) {
            val genre = if (appViewModel.getMovieListType() == "Популярные") {
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
            val country = if (appViewModel.getMovieListType() == "Популярные") {
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
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    appViewModel.getMovieById(
                        requireActivity(),
                        movieDescriptionView,
                        failedInternetConnectionView
                    ).observe(requireActivity()) {
                        movieName.text = it.nameRu
                        movieDescription.text = it.description
                        movieGenres.text = getGenresLine(it.genres)
                        movieCountries.text = getCountriesLine(it.countries)
                        loadMoviePoster(it.posterUrl)
                    }
                }
            }
        }
    }

    private fun getMovieByIdFromDb(id: Int) {
        binding.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    appViewModel.getMovieByIdFromDb(id).observe(requireActivity()) {
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

    // отображение фрагмента со списком популярных фильмов при нажатии на кнопку "назад"
    private fun removeMovieDescriptionFragment() {
        (requireContext() as FragmentActivity).supportFragmentManager.popBackStack()

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            appViewModel.setSelectedMovieId(0)
    }
}