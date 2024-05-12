package com.example.mad_lab_04_app

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_lab_04_app.Model.ToDoModel
import com.example.mad_lab_04_app.ToDoAdapter.ToDoAdapter
import com.example.mad_lab_04_app.Utils.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity(), AddNewTask.DialogCloseListener {
    private var db: DatabaseHandler? = null
    private var tasksRecyclerView: RecyclerView? = null
    private var tasksAdapter: ToDoAdapter? = null
    private var fab: FloatingActionButton? = null
    private var taskList: List<ToDoModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        db?.openDatabase()

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView?.layoutManager = LinearLayoutManager(this)
        tasksAdapter = ToDoAdapter(db!!, this@MainActivity)
        tasksRecyclerView?.adapter = tasksAdapter

        fab = findViewById(R.id.fab)
        fab?.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter!!))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = db?.getAllTasks() ?: ArrayList()
        taskList = taskList.asReversed()
        tasksAdapter?.setTasks(taskList)
        tasksAdapter?.notifyDataSetChanged()
    }
}
