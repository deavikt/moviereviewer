package ru.tinkoff.moviereviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ru.tinkoff.moviereviewer.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding
    val tabNameList: List<String> = listOf("Популярные", "Избранное")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNameList[position]
        }.attach()

        return binding.root
    }

    private inner class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = tabNameList.size

        override fun createFragment(position: Int): Fragment = MovieListFragment(tabNameList[position])
    }
}