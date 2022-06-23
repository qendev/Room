package com.example.roomdatabaseapp.dao

import androidx.room.*
import com.example.roomdatabaseapp.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface EmployeeDao {

    @Insert
     suspend fun insert(employeeEntity: EmployeeEntity)

    @Update
    suspend fun update(employeeEntity: EmployeeEntity)

    @Delete
    suspend fun delete(employeeEntity: EmployeeEntity)

    @Query("Select * from `employee-table`")
    fun fetchAllEmployee():Flow<List<EmployeeEntity>>

    @Query("Select * from `employee-table` where id=:id")
    fun fetchEmployeeById(id:Int):Flow<EmployeeEntity>

}