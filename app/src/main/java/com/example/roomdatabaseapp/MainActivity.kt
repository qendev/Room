package com.example.roomdatabaseapp

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabaseapp.dao.EmployeeDao
import com.example.roomdatabaseapp.databinding.ActivityMainBinding
import com.example.roomdatabaseapp.databinding.DialogUpdateBinding
import com.example.roomdatabaseapp.entity.EmployeeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //set up the employeeDao using the employeeApp
        val employeeDao = (application as EmployeeApp).db.employDao()

        binding?.btnAdd?.setOnClickListener {
            //TODO call addRecord withemployeeDao
            addRecord(employeeDao)

        }

        //get the list of records from the background
        lifecycleScope.launch {
            employeeDao.fetchAllEmployee().collect {
                //create an arrayList
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
        }
    }

    private fun addRecord(employeeDao: EmployeeDao) {
        //get the name and email entered
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            //since we are going to do this in the background,we need to introduce the coroutines
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    employeeDao.insert(EmployeeEntity(name = name, email = email,))
                }
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()

            }
        } else {
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun setupListOfDataIntoRecyclerView(
        employeeList: ArrayList<EmployeeEntity>,
        employeeDao: EmployeeDao
    ) {
        //first check if the employeeList is empty
        if (employeeList.isNotEmpty()) {
            val itemAdapter = ItemAdapter(employeeList,
                { updateId ->
                    updateRecordDialog(updateId, employeeDao)
                },
                { deleteId ->
                    deleteRecordAlertDialog(deleteId, employeeDao)
                }

            )
            //assign a layount for the recyclerView
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            //assign the adpater to the receyclerView
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
            binding?.rvItemsList
        } else {
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if (it!=null){
                    binding?.etUpdateName.setText(it.name)
                    binding?.etUpdateEmailId.setText(it.email)

                }

            }
        }

        binding.tvUpdate.setOnClickListener {
            //to get the text from both the name and emeil editTexts
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            //to check if the two fields are not empty
            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    //when creating you do not need to pass in the id but when you are making changes,you pass in the id
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()

    }

    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao) {
        //first create the alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //set and code the positive button functionality
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id,"",""))
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss() // Dialog will be dismissed
            }

        }
        //set and code the negative functionality
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


    }
}