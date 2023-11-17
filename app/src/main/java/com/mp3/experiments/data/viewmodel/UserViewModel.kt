package com.mp3.experiments.data.viewmodel

import android.util.Log
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
import com.mp3.experiments.data.model.TicketModel
import com.mp3.experiments.data.model.UserDetailsModel
import com.mp3.experiments.data.model.UserModel
import com.mp3.experiments.data.nodes.NODE_PROFILE_IMAGES
import com.mp3.experiments.data.nodes.NODE_TICKETS
import com.mp3.experiments.data.nodes.NODE_USERS
import com.mp3.experiments.data.nodes.NODE_USER_DETAILS
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.states.StorageStates

class UserViewModel : ViewModel(){
    private val auth = Firebase.auth
    private val firebase_database = Firebase.database.reference
    private var firebase_storage = Firebase.storage.reference

    private var auth_states = MutableLiveData<AuthenticationStates>()
    private var storage_states = MutableLiveData<StorageStates>()

    private var _tickets = MutableLiveData<List<TicketModel>>()
    val tickets : LiveData<List<TicketModel>> get() = _tickets
    fun getAuthStates(): LiveData<AuthenticationStates> = auth_states

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
        username : String,
        user_nickname : String,
        first_name : String,
        last_name : String,
        email : String,
        user_age : Int,
        user_account_date_created : String,
        img : ByteArray
    ) {
        val profileRef = firebase_storage
            .child(NODE_PROFILE_IMAGES)
            .child(auth.currentUser?.uid + ".jpg")

        profileRef.putBytes(img).addOnSuccessListener {
            profileRef.downloadUrl.addOnSuccessListener { it ->
                val userDetails = UserDetailsModel(
                    username,
                    user_nickname,
                    first_name,
                    last_name,
                    email,
                    user_age,
                    user_account_date_created,
                    0,
                    it.toString())

                val user = UserModel(userDetails, null)

                firebase_database.child(NODE_USERS)
                    .child(auth.currentUser?.uid!!)
                    .setValue(user)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            auth_states.value = AuthenticationStates.ProfileUpdated
                        }
                        else auth_states.value = AuthenticationStates.Error
                    }
            }.addOnFailureListener {
                storage_states.value = StorageStates.StorageFailed(it.message)
            }
        }.addOnFailureListener {
            storage_states.value = StorageStates.StorageFailed(it.message)
        }
    }

    fun addTicket(ticket: TicketModel){
        val ticketRef = firebase_database
            .child(NODE_USERS)
            .child(auth.currentUser?.uid!!)
            .child(NODE_TICKETS)

        val newKey = ticketRef.push().key
        updateUserTicketCount(ticket.number_of_selected_seats!!)

        ticketRef.child(newKey!!).setValue(ticket)
    }

    fun updateUserTicketCount(numOfTickets : Int){
        val userRef = firebase_database
            .child(NODE_USERS)
            .child(auth.currentUser?.uid!!)
            .child(NODE_USER_DETAILS)

        userRef.child("tickets_bought").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val currentTicketCount = dataSnapshot.getValue(Int::class.java) ?: 0

                val updatedTicketCount = currentTicketCount + numOfTickets

                userRef.child("tickets_bought").setValue(updatedTicketCount)
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                }
            }
        )


    }

    fun observeTickets(){
        val ticketRef = firebase_database
            .child(NODE_USERS)
            .child(auth.currentUser?.uid!!)
            .child(NODE_TICKETS)

        val ticketsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ticketModel : MutableList<TicketModel> = mutableListOf()
                for (ticketSnapshot in dataSnapshot.children) {
                    val ticket = ticketSnapshot.getValue<TicketModel>()

                    ticketModel.add(ticket!!)
                }

                _tickets.value = ticketModel
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TicketObserver", "Failed to get tickets")
            }
        }
        ticketRef.addValueEventListener(ticketsListener)
    }

    fun logOut() {
        auth.signOut()
        auth_states.value = AuthenticationStates.LogOut
    }

}