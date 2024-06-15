package ru.tinkoff.moviereviewer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.tinkoff.moviereviewer.databinding.FragmentMovieListBinding

class MovieListFragment: Fragment() {

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("movie list type", appViewModel.getMovieListType())

        if (appViewModel.getMovieListType() == "Популярные")
            getPopularMovieList()
        else
            getFavouriteMovieList()
    }

    private fun getPopularMovieList() {
        binding.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    appViewModel.getPopularMovieList(movieList, failedInternetConnectionView).observe(requireActivity()) {
                        movieList.adapter = MovieListAdapter(it, appViewModel)
                    }
                }
            }
        }
    }

    private fun getFavouriteMovieList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                appViewModel.getFavouriteMovieList().observe(requireActivity()) {
                    binding.movieList.adapter = MovieListAdapter(it, appViewModel)
                }
            }
        }
    }
}