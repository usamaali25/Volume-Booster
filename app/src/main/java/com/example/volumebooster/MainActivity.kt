package com.example.volumebooster

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.volumebooster.databinding.ActivityMainBinding
import com.example.volumebooster.parentFragments.Equalizers
import com.example.volumebooster.parentFragments.Home
import com.example.volumebooster.parentFragments.PlayList
import com.example.volumebooster.parentFragments.Themes
import com.example.volumebooster.services.MusicServices


class MainActivity : AppCompatActivity() {

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
        private lateinit var binding: ActivityMainBinding
    }
    lateinit var progressDialog:ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        val homeFragment = Home()
        val playList = PlayList()
        val equalizer = Equalizers()
        val themes = Themes()

        val intent = Intent(this, MusicServices::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }

       supportFragmentManager.beginTransaction().replace(R.id.flFragment,homeFragment).commit()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home ->{
                    setCurrentFragment(homeFragment)

                    // Respond to navigation item 1 click
                    true
                }
                R.id.playlist -> {

                    progressDialog= ProgressDialog(this)
                    progressDialog.setTitle("Please wait")
                    progressDialog.setMessage("Data is loading, please wait")
                    progressDialog.setCancelable(true)
                    progressDialog.show()
                    setCurrentFragment(playList)
                    // Respond to navigation item 2 click
                    true
                }

                R.id.equalizer -> {
                    /*val intent = Intent(this,EqualizerActivity::class.java)
                    startActivity(intent)*/
                    setCurrentFragment(equalizer)
                    // Respond to navigation item 2 click
                    true
                }
                R.id.theme -> {
                    setCurrentFragment(themes)
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (checkPermission()) {
            //main logic or main code
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.storagePermission.visibility = View.GONE
            // . write your main code to execute, It will execute if the permission is already given.
        } else {
            requestPermission()
        }
    }


    private fun setCurrentFragment(fragment: Fragment)=supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    private fun checkPermission(): Boolean {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty()) {
            return
        }
        var allPermissionsGranted = true
        if (grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
        }
        if (!allPermissionsGranted) {
            var somePermissionsForeverDenied = false
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)) {
                    //denied
                    Log.e("deniedu", permission)
                    binding.storagePermission.visibility = View.VISIBLE
                    binding.storagePermission.setOnClickListener { requestPermission() }
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //allowed
                        Log.e("allowed", permission)
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission)
                        somePermissionsForeverDenied = true
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                binding.storagePermission.setOnClickListener {
                    val alertDialogBuilder =
                        AlertDialog.Builder(this@MainActivity)
                    alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage(
                            "You have forcefully denied some of the required permissions " +
                                    "for this action. Please open settings, go to permissions and allow them."
                        )
                        .setPositiveButton(
                            "Settings"
                        ) { dialog: DialogInterface?, which: Int ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(
                            "Cancel"
                        ) { dialog: DialogInterface?, which: Int -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.storagePermission.visibility = View.GONE
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



}