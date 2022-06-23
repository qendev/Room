package com.example.roomdatabaseapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdatabaseapp.dao.EmployeeDao
import com.example.roomdatabaseapp.entity.EmployeeEntity

@Database(entities = [EmployeeEntity::class], version = 1)
abstract class EmployeeDatabase:RoomDatabase() {

    //connect the database to the dao
    abstract fun employDao():EmployeeDao

    //add a companion object that allows us to add functions to the employee database class

    companion object{

        @Volatile
        private  var INSTANCE:EmployeeDatabase? = null

        //create a helper function to get a database
        fun getInstance(context: Context):EmployeeDatabase{
            //to make sure only one thread may enter a synchronized block at a time
            synchronized(this){
                var instance = INSTANCE

                if (instance== null){
                    instance = Room.databaseBuilder(context.applicationContext, EmployeeDatabase::class.java,
                        "employee_database")
//                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return  instance

            }
        }
    }
}