package com.dianascode.sweatworks.models

import com.google.gson.Gson
import okhttp3.ResponseBody
import java.io.Serializable

class SweatWorksError: Serializable, Exception() {
    var error : String? = ""
    override var message: String? = ""

    companion object {
        fun create(raw: ResponseBody?) : SweatWorksError? {
            var swError: SweatWorksError? = null
            try {
                swError = Gson().fromJson<SweatWorksError>(raw!!.string(), SweatWorksError::class.java)

            } catch (e: Exception) {
                swError = null
            } finally {
                return swError
            }
        }
    }
}