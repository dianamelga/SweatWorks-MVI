package com.dianascode.sweatworks.utils

import com.dianascode.sweatworks.SweatWorks
import com.dianascode.sweatworks.repository.IRandomUserRepository
import com.dianascode.sweatworks.repository.RandomUserRepository
import com.dianascode.sweatworks.repository.SweatWorksRepository
import py.com.bancop.app.utils.schedulers.BaseSchedulerProvider
import py.com.bancop.app.utils.schedulers.SchedulerProvider
import java.lang.IllegalArgumentException

object Injection {
    fun <T: IRandomUserRepository> provideRepository(repositoryClass: Class<T>): SweatWorksRepository {
        return when(repositoryClass) {
            RandomUserRepository::class.java -> RandomUserRepository(SweatWorks.getInstance())
            else -> throw IllegalArgumentException("Unknown repository class $repositoryClass")
        }
    }

    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider
}