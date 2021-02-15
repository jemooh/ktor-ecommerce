package com.skyz.database.injection

import com.skyz.database.dao.UserDao
import com.skyz.database.dao.Users
import org.koin.dsl.module

object DaoInjection {
    val koinBeans= module {
        single<UserDao> { Users }
    }
}