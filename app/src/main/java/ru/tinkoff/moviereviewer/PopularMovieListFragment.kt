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
import ru.tinkoff.moviereviewer.databinding.FragmentPopularMovieListBinding

class PopularMovieListFragment : Fragment() {

    private lateinit var binding: FragmentPopularMovieListBinding
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopularMovieListBinding.inflate(inflater)

        binding.popularMovieList.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        appViewModel.getPopularMovieList().observe(requireActivity()) {
            lifecycleScope.launch {
                binding.popularMovieList.adapter = PopularMovieListAdapter(it)
            }
        }

        return binding.root
    }
}