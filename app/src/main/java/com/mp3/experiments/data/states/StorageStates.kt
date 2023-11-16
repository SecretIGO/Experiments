package com.mp3.experiments.data.states

sealed class StorageStates {

    data object StorageSuccess : StorageStates()
    data class GetUrlSuccess(val url : String) : StorageStates()
    data class StorageFailed(val message: String?) : StorageStates()

}
