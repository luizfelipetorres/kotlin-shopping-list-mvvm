package com.lftf.shoppinglist

import android.os.Bundle
import android.util.Log
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
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.viewmodel.MainViewModel

const val TAG = "D/MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val listViewModel: MainViewModel by viewModels() {
        MainViewModel.Companion.Factory(ItemRepository(this))
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        listViewModel.totalAmount.observe(this) {
            val strTotal = getString(R.string.price).format(it)
            menu.findItem(R.id.MoneyFragment).title = strTotal
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.MoneyFragment -> item.onNavDestinationSelected(navController)
            else -> onSupportNavigateUp()
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(
            TAG,
            "${navController.currentDestination?.id.toString()} == ${navController.findDestination(R.id.MoneyFragment)?.id}"
        )
//
//        if (navController.currentDestination?.id == navController.findDestination(R.id.MoneyFragment)?.id) {
//
//        }
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}