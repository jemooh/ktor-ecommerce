package com.skyz.api.user

import com.skyz.model.PostUserBody
import com.skyz.model.User

interface UserApi {
    fun getUserById(Id:Int) : User?
    fun getUserByUsername(username: String)  : User?
    fun removeUser(userId: Int) : Boolean
    fun createUser(postUser: PostUserBody): User ?
}