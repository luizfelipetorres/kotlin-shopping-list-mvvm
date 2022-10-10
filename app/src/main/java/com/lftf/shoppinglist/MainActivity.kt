package com.lftf.shoppinglist

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.lftf.shoppinglist.databinding.ActivityMainBinding
import com.lftf.shoppinglist.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val listViewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isPermissionGranted = it ?: isPermissionGranted
            }

        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        listViewModel.getAll()
    }

    private fun checkPermissions() {
        isPermissionGranted =
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED


        if (!isPermissionGranted) {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }

        Log.d("MainActivity", isPermissionGranted.toString())
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
        when(item.itemId){
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