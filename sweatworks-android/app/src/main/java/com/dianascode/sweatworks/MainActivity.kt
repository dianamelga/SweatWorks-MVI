package com.dianascode.sweatworks

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.modules.home.HomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        Handler().postDelayed({
            val i = Intent(this, HomeActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(i)
            }
            finish()
        }, 3000)
    }
}
