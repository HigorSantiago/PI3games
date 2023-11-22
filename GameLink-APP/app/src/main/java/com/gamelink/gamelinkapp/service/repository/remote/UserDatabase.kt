package com.gamelink.gamelinkapp.service.repository.remote

import android.util.Log
import com.gamelink.gamelinkapp.service.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDatabase {
    private val remote = RetrofitClient.getService(UserService::class.java)

    suspend fun create(user: UserModel): UserModel {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.create(user)

                if(response.code() != 201){
                    throw Exception("Usuário já cadastrado")
                }

                return@withContext response.body()!!
            } catch(error: Exception) {
                Log.d("UserDatabase", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }
}