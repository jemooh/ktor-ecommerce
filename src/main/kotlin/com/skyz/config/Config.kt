package com.skyz.config

class Config(
    val host: String,
    val port: Int,
    val databaseHost: String,
    val databasePort: String

) {
    companion object{
        const val DATABASENAME: String = "ktorecommercedb"
        const val DATABASEUSER: String = "ktorecommerceuser"
        const val DATABASEPASSWORD: String = "ktorecommercepassword"
    }
}