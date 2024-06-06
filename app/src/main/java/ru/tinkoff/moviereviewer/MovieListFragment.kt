package ru.tinkoff.moviereviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.FragmentMovieListBinding

class MovieListFragment(private val tabName: String): Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater)

        binding.movieList.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        if (tabName == "Популярные")
            getPopularMovieList()
        else
            getFavouriteMovieList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (tabName == "Популярные")
            getPopularMovieList()
        else
            getFavouriteMovieList()
    }

    private fun getPopularMovieList() {
        binding.apply {
            appViewModel.getPopularMovieList(movieList, failedInternetConnectionView).observe(requireActivity()) {
                lifecycleScope.launch{
                    movieList.adapter = MovieListAdapter(tabName, it, appViewModel)
                }
            }
        }
    }

    private fun getFavouriteMovieList() {
        appViewModel.getFavouriteMovieList().observe(requireActivity()) {
            lifecycleScope.launch {
                binding.movieList.adapter = MovieListAdapter(tabName, it, appViewModel)
            }
        }
    }
}