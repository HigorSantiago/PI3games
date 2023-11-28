package com.gamelink.gamelinkapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gamelink.gamelinkapp.R
import com.gamelink.gamelinkapp.service.constants.GameLinkConstants
import com.gamelink.gamelinkapp.service.listener.APIListener
import com.gamelink.gamelinkapp.service.model.ProfileModel
import com.gamelink.gamelinkapp.service.model.UserModel
import com.gamelink.gamelinkapp.service.model.ValidationModel
import com.gamelink.gamelinkapp.service.repository.ProfileRepository
import com.gamelink.gamelinkapp.service.repository.SecurityPreferences
import com.gamelink.gamelinkapp.service.repository.UserRepository
import com.gamelink.gamelinkapp.service.repository.remote.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application.applicationContext)
    private val profileRepository = ProfileRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _usernameFieldErrorResId = MutableLiveData<Int>()
    val usernameFieldErrorResId: LiveData<Int> = _usernameFieldErrorResId

    private val _usernameHelperErrorResId = MutableLiveData<Int?>()
    val usernameHelperErrorResId: LiveData<Int?> = _usernameHelperErrorResId

    private val _passwordFieldErrorResId = MutableLiveData<Int>()
    val passwordFieldErrorResId: LiveData<Int> = _passwordFieldErrorResId

    private val _passwordHelperErrorResId = MutableLiveData<Int?>()
    val passwordHelperErrorResId: LiveData<Int?> = _passwordHelperErrorResId

    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser

    private val _hasNotProfile = MutableLiveData<Boolean>()
    val hasNotProfile: LiveData<Boolean> = _hasNotProfile


    private var isFormValid = false

    fun login(username: String, password: String) {
        isFormValid = true

        _usernameFieldErrorResId.value = getDrawableResIdIfNull(username)
        _usernameHelperErrorResId.value = getErrorStringResIdIfEmpty(username)

        _passwordFieldErrorResId.value = getDrawableResIdIfNull(password)
        _passwordHelperErrorResId.value = getErrorStringResIdIfEmpty(password)

        if (isFormValid) {
            viewModelScope.launch {
                val user = UserModel().apply {
                    this.username = username
                    this.password = password
                }

                userRepository.login(user, object : APIListener<UserModel> {
                    override fun onSuccess(result: UserModel) {
                        securityPreferences.store(GameLinkConstants.SHARED.TOKEN_KEY, result.token)
                        securityPreferences.store(GameLinkConstants.SHARED.USERNAME, username)
                        securityPreferences.store(GameLinkConstants.SHARED.USER_ID, result.userId)

                        RetrofitClient.addHeaders(result.token)

                        _login.value = ValidationModel()
                    }

                    override fun onFailure(message: String) {
                        _login.value = ValidationModel(message)
                    }

                })
            }
        }
    }

    fun verifyLoggedUser() {
        securityPreferences.remove(GameLinkConstants.SHARED.USER_ID)
        securityPreferences.remove(GameLinkConstants.SHARED.USERNAME)
        securityPreferences.remove(GameLinkConstants.SHARED.TOKEN_KEY)
//        val token = securityPreferences.get(GameLinkConstants.SHARED.TOKEN_KEY)
//
//        val logged = token != ""
//
//        if(logged) {
//            RetrofitClient.addHeaders(token)
//        }
        _loggedUser.value = false
    }

    fun verifyHasNotProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getByUser()

            _hasNotProfile.value = profile == null
        }
    }

    private fun getErrorStringResIdIfEmpty(value: String): Int? {
        return if (value.isEmpty()) {
            isFormValid = false
            R.string.required_field_error
        } else null
    }

    private fun getDrawableResIdIfNull(value: String): Int {
        return if (value.isEmpty()) {
            isFormValid = false
            R.drawable.rounded_edit_text_error
        } else R.drawable.rounded_edit_text
    }
}