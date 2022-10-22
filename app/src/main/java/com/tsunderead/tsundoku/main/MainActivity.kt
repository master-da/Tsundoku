package com.tsunderead.tsundoku.main

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tsunderead.tsundoku.R
import com.tsunderead.tsundoku.databinding.ActivityMainBinding
import com.tsunderead.tsundoku.manga_card_cell.Manga
import com.tsunderead.tsundoku.manga_card_cell.mangaList
import com.tsunderead.tsundoku.offlinedb.LibraryDBHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, _, _ ->
            showBottomNav(bottomNavigationView, true)
        }
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun showBottomNav(bottomNavigationView: BottomNavigationView, isVisible: Boolean) {
        val layoutParams: ViewGroup.LayoutParams = bottomNavigationView.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = layoutParams.behavior
            if (behavior is HideBottomViewOnScrollBehavior) {
                if (isVisible) {
                    behavior.slideUp(bottomNavigationView)
                } else {
                    behavior.slideDown(bottomNavigationView)
                }
            }
        }
    }
}
