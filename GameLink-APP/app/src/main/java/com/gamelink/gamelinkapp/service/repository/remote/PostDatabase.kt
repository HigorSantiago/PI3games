package com.gamelink.gamelinkapp.service.repository.remote

import android.util.Log
import com.gamelink.gamelinkapp.service.model.PostModel
import com.gamelink.gamelinkapp.service.repository.remote.service.CommunityService
import com.gamelink.gamelinkapp.service.repository.remote.service.PostService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostDatabase {
    private val remote = RetrofitClient.getService(PostService::class.java)
    private val communityRemote = RetrofitClient.getService(CommunityService::class.java)

    suspend fun get(): List<PostModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.get()

                return@withContext response.body()!!
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase get", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun getRecommended(): List<PostModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.getRecommended()

                return@withContext response.body()!!
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase getRecommended", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun save(description: RequestBody, image: MultipartBody.Part?): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                val response = if (image == null)
                    remote.save(description)
                else
                    remote.saveWithImage(description, image)

                val s = ""
                if (response.code() != 201) {
                    throw Exception(response.errorBody()!!.string())
                }

                return@withContext true
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase save", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun saveEvent(post: PostModel): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.saveWithEvent(post)

                if (response.code() != 201) {
                    throw Exception(response.errorBody()!!.string())
                }

                return@withContext true
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase saveEvent", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun saveForCommunity(
        communityId: String,
        description: RequestBody,
        image: MultipartBody.Part?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                val response = if (image == null)
                    communityRemote.makePost(communityId, description)
                else
                    communityRemote.makePostWithImage(communityId, description, image)

                if (response.code() != 200) {
                    throw Exception(response.errorBody()!!.string())
                }

                return@withContext true
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase save", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun delete(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.delete(id)

                if (response.code() != 202) {
                    throw Exception(response.errorBody()!!.string())
                }

                return@withContext true
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase delete", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }

    suspend fun like(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = remote.like(id)

                if (response.code() != 202) {
                    throw Exception(response.errorBody()!!.string())
                }

                return@withContext response.body()!!
            } catch (ex: CancellationException) {
                throw ex
            } catch (error: Exception) {
                error.printStackTrace()
                Log.d("PostDatabase delete", error.message.toString())
                throw Exception(error.message.toString())
            }
        }
    }
}