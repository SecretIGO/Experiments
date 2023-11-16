package com.mp3.experiments.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mp3.experiments.data.model.UserModel
import com.mp3.experiments.data.nodes.NODE_PROFILE_IMAGES
import com.mp3.experiments.data.nodes.NODE_USERS
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.states.StorageStates

class UserViewModel : ViewModel(){
    private val auth = Firebase.auth
    private val firebase_database = Firebase.database.reference
    private var firebase_storage = Firebase.storage.reference

    fun getAuthStates(): LiveData<AuthenticationStates> = auth_states
    private var auth_states = MutableLiveData<AuthenticationStates>()

    fun isSignedIn() {
        auth_states.value = AuthenticationStates.IsSignedIn(auth.currentUser != null)
    }

    fun signUp_user(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) auth_states.value = AuthenticationStates.SignedUp
            else auth_states.value = AuthenticationStates.Error

        }.addOnFailureListener {
            auth_states.value = AuthenticationStates.Error
        }
    }

    fun signIn_user(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) auth_states.value = AuthenticationStates.SignedIn
            else auth_states.value = AuthenticationStates.Error
        }.addOnFailureListener {
            auth_states.value = AuthenticationStates.Error
        }
    }

    fun getUserProfile() {
        val objectListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.getValue<UserModel>()
                auth_states.value = AuthenticationStates.Default(users)
            }

            override fun onCancelled(error: DatabaseError) {
                auth_states.value = AuthenticationStates.Error
            }
        }
        firebase_database.child(NODE_USERS + "/" + auth.currentUser?.uid).addValueEventListener(objectListener)
    }

    fun createUserRecord(
        email : String,
        username : String,
        first_name : String,
        last_name : String,
        gender : String,
        img : ByteArray
    ) {
//        val userRef = firebase_storage.child(NODE_PROFILE_IMAGES + "/" + auth.currentUser?.uid + ".jpg")
//
//        userRef.putBytes(img).addOnSuccessListener {
//            userRef.downloadUrl.addOnSuccessListener { it ->
//                val user = UserModel()
//                firebase_storage.child(NODE_USERS + "/" + auth.currentUser?.uid).setValue(user).addOnCompleteListener {
//                    if(it.isSuccessful){
//                        auth_states.value = AuthenticationStates.ProfileUpdated
//                    }
//                    else auth_states.value = AuthenticationStates.Error
//                }
//
//            }.addOnFailureListener {
//                storage_states.value = StorageStates.StorageFailed(it.message)
//            }
//        }.addOnFailureListener {
//            storage_states.value = StorageStates.StorageFailed(it.message)
//        }
    }

}