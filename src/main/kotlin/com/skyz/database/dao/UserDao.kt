package com.skyz.database.dao

import com.skyz.model.PostUserBody
import com.skyz.model.PutUserBody
import com.skyz.model.User
import org.jetbrains.exposed.sql.*

object Users : Table(), UserDao {
    val id = integer("id").primaryKey().autoIncrement()
    val username = varchar("username", length = 255)
    val first_name = varchar("first_name", length = 255)
    val last_name = varchar("last_name", length = 255)
    val creationTime = long("creationTime")
    val password = varchar("password", 255)


    override fun getUserById(userId: Int): User? {
        return select {
            (id eq userId)
        }.mapNotNull {
            it.mapRowToUser()
        }.singleOrNull()
    }

    override fun insertUser(postUser: PostUserBody): Int? {
        return (insert {
            it[first_name] = postUser.first_name
            it[last_name] = postUser.last_name
            it[username] = postUser.username
            it[password] = postUser.password
        })[id]
    }

    override fun updateUser(userId: Int, putUser: PutUserBody): User? {
        TODO("Not yet implemented")
    }

    override fun deleteUser(userId: Int): Boolean {
        return deleteWhere { (id eq userId) } > 0
    }

    override fun getUserByName(usernameValue: String): User? {
        TODO("Not yet implemented")
    }

}



fun ResultRow.mapRowToUser() =
    User(
        id = this[Users.id],
        first_name = this[Users.first_name],
        last_name = this[Users.last_name],
        username = this[Users.username],
        password = this[Users.password]
    )


interface UserDao {
    fun getUserById(userId: Int): User?
    fun insertUser(postUser: PostUserBody): Int?
    fun updateUser(userId: Int, putUser: PutUserBody): User?
    fun deleteUser(userId: Int): Boolean
    fun getUserByName(usernameValue: String): User?

}