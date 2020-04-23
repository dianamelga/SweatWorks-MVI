package com.dianascode.sweatworks.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dianascode.sweatworks.modules.home.HomeProcessorHolder
import com.dianascode.sweatworks.modules.home.HomeViewModel
import com.dianascode.sweatworks.modules.userDetail.UserDetailProcessorHolder
import com.dianascode.sweatworks.modules.userDetail.UserDetailViewModel
import com.dianascode.sweatworks.repository.RandomUserRepository

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
@Suppress("UNCHECKED_CAST")
class SweatWorksViewModelFactory private constructor(
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                HomeViewModel(
                    HomeProcessorHolder(
                        repository = Injection.provideRepository(RandomUserRepository::class.java) as RandomUserRepository,
                        schedulerProvider = Injection.provideSchedulerProvider()
                    )
                ) as T
            }
            UserDetailViewModel::class.java -> {
                UserDetailViewModel(
                    UserDetailProcessorHolder(
                        repository = Injection.provideRepository(RandomUserRepository::class.java) as RandomUserRepository,
                        schedulerProvider = Injection.provideSchedulerProvider()
                    )
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown model class $modelClass")
            }
        }
    }

    companion object : SingletonHolderSingleArg<SweatWorksViewModelFactory, Context>
        (::SweatWorksViewModelFactory)

}