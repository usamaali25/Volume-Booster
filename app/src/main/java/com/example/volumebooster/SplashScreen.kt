package com.example.volumebooster

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat

class SplashScreen : AppCompatActivity() {

    object Staticated {
        var SPLASH_TIME_OUT = 1000
    }


    var permissionsString = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash_screen)
            if (!hasPermissions(this@SplashScreen, *permissionsString)) {
                ActivityCompat.requestPermissions(this@SplashScreen, permissionsString, 131)
                //Do nothing here because permissions aren't granted

            } else {
                Handler().postDelayed({
                    val startAct = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(startAct)
                    // close this activity
                    this.finish()
                }, SplashScreen.Staticated.SPLASH_TIME_OUT.toLong())
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Handler().postDelayed({
                        /* Create an Intent that will start the Menu-Activity. */
                        val mainIntent = Intent(this@SplashScreen, MainActivity::class.java)
                        this.startActivity(mainIntent)
                        this.finish()
                    }, SplashScreen.Staticated.SPLASH_TIME_OUT.toLong())


                } else {
                    Toast.makeText(this, "Please grant all permissions to continue", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@SplashScreen, "Something went wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }


        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        var hasAllPermissions = true
        for (permission in permissions) {
            //return false instead of assigning, but with this you can log all permission values
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
            }
        }

        return hasAllPermissions

    }
}