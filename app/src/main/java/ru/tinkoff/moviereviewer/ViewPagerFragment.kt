package ru.tinkoff.moviereviewer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.tinkoff.moviereviewer.databinding.FragmentViewPagerBinding

class ViewPagerFragment: Fragment() {

    private lateinit var binding: FragmentViewPagerBinding
    private val appViewModel: AppViewModel by activityViewModels()
    private val tabNameList: List<String> = listOf("Популярные", "Избранное")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater)

        addToolBar()
        setToolBarTitle(tabNameList[0])

        binding.viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabNameList[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setToolBarTitle(tab?.text)
                appViewModel.setMovieListType(tab?.text.toString())
                Log.d("movie type list", appViewModel.getMovieListType())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        return binding.root
    }

    private fun addToolBar() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean { return false }
        })
    }

    private fun setToolBarTitle(title: CharSequence?) { binding.toolBar.title = title }

    private inner class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = tabNameList.size

        override fun createFragment(position: Int): Fragment = MovieListFragment()
    }
}