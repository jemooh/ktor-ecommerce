package com.skyz.api.user

import com.skyz.database.dao.UserDao
import com.skyz.model.PostUserBody
import com.skyz.model.User
import com.skyz.util.PasswordManagerContract
import org.koin.core.KoinComponent
import org.koin.core.inject

object UserApiImpl : UserApi, KoinComponent {

    private val usersDao by inject<UserDao>()
    private val passwordEncryption: PasswordManagerContract by inject()

    override fun getUserById(id: Int): User? {
        return usersDao.getUserById(id)
    }

    override fun getUserByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

    override fun removeUser(userId: Int): Boolean {
       return usersDao.deleteUser(userId)
    }

    override fun createUser(postUser: PostUserBody): User? {
        val encryptedUser = postUser.copy(password = passwordEncryption.encryptPassword(postUser.password))
        val key: Int ? = usersDao.insertUser(encryptedUser)
        return key?.let {
            usersDao.getUserById(it)
        } ?: throw InvalidUserException("Error while creating user")
    }

}