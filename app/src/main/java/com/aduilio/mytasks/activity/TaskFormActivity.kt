package com.aduilio.mytasks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aduilio.mytasks.databinding.ActivityTaskFormBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.service.TaskService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TaskFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskFormBinding

    private val taskService: TaskService by viewModels()

    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getLongExtra("task_id", -1)

        if (taskId != -1L) {
            taskService.readById(taskId!!).observe(this) { responseDto ->
                if (responseDto.isError) {
                    Toast.makeText(this, "Erro ao carregar a tarefa", Toast.LENGTH_SHORT).show()
                } else {
                    responseDto.value?.let { task ->
                        binding.etTitle.setText(task.title)
                        binding.etDescription.setText(task.description)
                        binding.etDate.setText(task.date?.toString())
                        binding.etTime.setText(task.time?.toString())
                    }
                }
            }
        }

        initComponents()
        setValues()
    }

    private fun initComponents() {
        binding.btSave.setOnClickListener {
            if (validateForm()) {
                val task = Task(
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    date = if (binding.etDate.text.isNullOrEmpty()) null else LocalDate.parse(binding.etDate.text.toString()),
                    time = if (binding.etTime.text.isNullOrEmpty()) null else LocalTime.parse(binding.etTime.text.toString()),
                    id = taskId
                )
                taskService.save(task).observe(this) { responseDto ->
                    if (responseDto.isError) {
                        Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(date, formatter)
            true
        } catch (e: DateTimeParseException) {
            true
        }
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(time, formatter)
            true
        } catch (e: DateTimeParseException) {
            true
        }
    }

    private fun validateForm(): Boolean {
        if (binding.etTitle.text.isNullOrEmpty()) {
            Toast.makeText(this, "O título é obrigatório", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!binding.etDate.text.isNullOrEmpty()) {
            if (!isValidDate(binding.etDate.text.toString())) {
                Toast.makeText(this, "Formato de data inválido. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (!binding.etTime.text.isNullOrEmpty()) {
            if (!isValidTime(binding.etTime.text.toString())) {
                Toast.makeText(this, "Formato de hora inválido. Use HH:mm.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    @Suppress("deprecation")
    private fun setValues() {
        (intent.extras?.getSerializable("task") as Task?)?.let { task ->
            taskId = task.id
            binding.etTitle.setText(task.title)
            binding.etDescription.setText(task.description)
            binding.etDate.setText(task.date?.toString())
            binding.etTime.setText(task.time?.toString())

            if (task.completed) {
                binding.btSave.visibility = View.INVISIBLE
            }
        } ?: run {
            taskId = null
        }
    }

    override fun onStart() {
        super.onStart()

        Log.e("lifecycle", "TaskForm onStart")
    }

    override fun onResume() {
        super.onResume()

        Log.e("lifecycle", "TaskForm onResume")
    }

    override fun onStop() {
        super.onStop()

        Log.e("lifecycle", "TaskForm onStop")
    }

    override fun onPause() {
        super.onPause()

        Log.e("lifecycle", "TaskForm onPause")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("lifecycle", "TaskForm onDestroy")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}