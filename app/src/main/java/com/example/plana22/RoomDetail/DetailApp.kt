package com.example.plana22.RoomDetail

import android.app.Application

/**This is as the repository.
 * It gets all the functions from the Dao class*/
class DetailApp : Application() {
    val db  by lazy {
        DetailDatabase.getInstance(this)
    }

    suspend fun addCategoryRepo(detailEntity: DetailEntity) =
        db.detailDao().insert(detailEntity)

    suspend fun deleteCategoryRepo(detailEntity: DetailEntity) =
        db.detailDao().delete(detailEntity)

    fun fetchAllCategoryVM(detailEntity: DetailEntity) =
        db.detailDao().fetchAllTaskCategory()

    fun checkIfExistsRepo(category: String) = db.detailDao().exists(category)


}