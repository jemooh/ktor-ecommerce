package com.skyz.api.injection

import com.skyz.api.user.UserApi
import com.skyz.api.user.UserApiImpl
import org.koin.dsl.module

object ApiInjection {
    val koinBeans = module {
        single<UserApi> { UserApiImpl }
    }
}