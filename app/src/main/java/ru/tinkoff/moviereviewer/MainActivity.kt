package ru.tinkoff.moviereviewer

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.tinkoff.moviereviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null)
            addViewPagerFragment()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            addMovieDescriptionFragment()
    }

    // добавление слайдера фрагментов в контейнер
    private fun addViewPagerFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, ViewPagerFragment())
            .commit()
    }

    // добавление фрагмента с описанием выбранного фильма в контейнер
    private fun addMovieDescriptionFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.movie_description_fragment_container, MovieDescriptionFragment())
            .commit()
    }
}