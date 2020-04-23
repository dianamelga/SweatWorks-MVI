package com.dianascode.sweatworks.modules.base

import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.models.SweatWorksError
import com.dianascode.sweatworks.utils.UtilTools
import retrofit2.HttpException

open class SweatWorksActivity : AppCompatActivity() {


    fun handleError(e: Throwable) {
        if (e is HttpException) {
            val error = SweatWorksError.create(e.response()?.errorBody())
            error?.let {
                    showMessage(it.message)
            } ?: showMessage(getString(R.string.conexion_failed_msg))
        } else {
            showMessage(getString(R.string.conexion_failed_msg))
        }
    }

    private fun showMessage(message: String?) {
        UtilTools.showDialog(this, message ?: getString(R.string.generic_error_msg))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                android.R.id.home -> {
                    super.onBackPressed()
                }
                else -> {
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun requestToBeLayoutFullscreen() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }


}
