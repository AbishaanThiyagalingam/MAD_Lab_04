package com.example.mad_lab_04_app.ToDoAdapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_lab_04_app.AddNewTask
import com.example.mad_lab_04_app.MainActivity
import com.example.mad_lab_04_app.Model.ToDoModel
import com.example.mad_lab_04_app.R
import com.example.mad_lab_04_app.Utils.DatabaseHandler


class ToDoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {
    private var todoList: List<ToDoModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()
        val item: ToDoModel = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = toBoolean(item.status)
        holder.task.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
        }
    }

    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    val context: Context
        get() = activity

    fun setTasks(todoList: List<ToDoModel>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item: ToDoModel = todoList[position]
        db.deleteTask(item.id)
        todoList = todoList.minus(item)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item: ToDoModel = todoList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
        }
        val fragment = AddNewTask().apply {
            arguments = bundle
        }
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox = view.findViewById(R.id.todoCheckBox)
    }
}

