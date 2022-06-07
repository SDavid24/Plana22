package com.example.plana22.Models

import android.os.Parcel
import android.os.Parcelable


data class User(
    var id : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val email : String = "",
    val mobile : Long = 0,
    val image : String = "",
    val fcmToken : String = "",
    var selected : Boolean = false
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong(),
        source.readString()!!,
        source.readString()!!,
        source.readBoolean()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(firstName)
        writeString(lastName)
        writeString(email)
        writeLong(mobile)
        writeString(image)
        writeString(fcmToken)
        writeBoolean(selected)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}