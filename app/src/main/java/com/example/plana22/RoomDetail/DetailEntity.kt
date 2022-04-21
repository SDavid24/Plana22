package com.example.plana22.RoomDetail

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

@Entity(tableName = "detail-table")
data class DetailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "Image") var image: Int? = null,
    @ColumnInfo(name = "Task amount ") var taskAmount: Int? = null,
    @ColumnInfo(name = "Category") var category: String = "",
    @ColumnInfo(name = "Task list") var taskList: MutableList<TaskList> = mutableListOf<TaskList>()
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }
}

data class TaskList(val tasks : String): Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }
}

class TaskListConverter{
    @TypeConverter
    fun listToJson(value: MutableList<TaskList>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): MutableList<TaskList>? {

        return if (value.isEmpty()){
            emptyList<TaskList>().toMutableList()
        }else{
            val listType = object : TypeToken<MutableList<TaskList>>() {

            }.type
            Gson().fromJson(value,listType)
        }
    }
}
