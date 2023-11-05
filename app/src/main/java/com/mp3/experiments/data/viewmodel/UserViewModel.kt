package com.mp3.experiments.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mp3.experiments.data.model.UserModel

class UserViewModel : ViewModel(){
    private val firebase_database = Firebase.database.reference
    private var firebase_storage = Firebase.storage.reference

    private val _users = MutableLiveData<UserModel>()
    val users: LiveData<UserModel> get() = _users


}