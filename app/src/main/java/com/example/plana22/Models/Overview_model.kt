package com.example.plana22.Models

import java.io.Serializable

data class Overview_model(
    val id : Int,
    var image : Int,
    var category : String,
    var taskAmount : String
) : Serializable
