package com.dianascode.sweatworks.modules.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.models.SweatWorksError
import com.dianascode.sweatworks.utils.UtilTools
import retrofit2.HttpException

open class SweatWorksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
}
