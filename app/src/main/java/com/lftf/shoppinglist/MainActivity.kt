package com.lftf.shoppinglist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.lftf.shoppinglist.databinding.ActivityMainBinding
import com.lftf.shoppinglist.model.TotalValues
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.viewmodel.MainViewModel

const val TAG = "D/MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val listViewModel: MainViewModel by viewModels {
        MainViewModel.Companion.Factory(
            ItemRepository(this),
            MoneyRepository(this)
        )
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onResume() {
        super.onResume()
        listViewModel.getAll()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        listViewModel.totalValues.observe(this) {
            changeTotal(it, menu)
        }
        return false
    }

    private fun changeTotal(totalValues: TotalValues, menu: Menu) {
        val remaining = totalValues.totalLimit - totalValues.totalAmount
        val strTotal = getString(R.string.total_price).format(totalValues.totalAmount, remaining)
        menu.findItem(R.id.MoneyFragment).title = strTotal
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.MoneyFragment -> item.onNavDestinationSelected(navController)
            else -> onSupportNavigateUp()
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}