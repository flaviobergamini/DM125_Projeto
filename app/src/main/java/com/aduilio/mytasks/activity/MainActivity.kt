package com.aduilio.mytasks.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.aduilio.mytasks.R
import com.aduilio.mytasks.adapter.TaskItemTouchCallback
import com.aduilio.mytasks.adapter.TasksAdapter
import com.aduilio.mytasks.databinding.ActivityMainBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.fragment.PreferenceFragment
import com.aduilio.mytasks.helper.NotificationHelper
import com.aduilio.mytasks.listener.TaskItemClickListener
import com.aduilio.mytasks.listener.TaskItemSwipeListener
import com.aduilio.mytasks.service.TaskService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var tasksAdapter: TasksAdapter

    private val taskService: TaskService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("lifecycle", "Main onCreate")

        initComponents()

        askNotificationPermission()

        if (Firebase.auth.currentUser == null) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        savedInstanceState?.let { state ->
            state.keySet().forEach { key -> Log.e("state", key) }
        }

        val helper = NotificationHelper(this)
//        helper.showNotification("Titulo da notificação 1", "Texto da notificação")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("minhaChave", "meuValor")
    }

    override fun onStart() {
        super.onStart()

        Log.e("lifecycle", "Main onStart")
    }

    override fun onResume() {
        super.onResume()

        Log.e("lifecycle", "Main onResume")

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PreferenceFragment.DAILY_NOTIFICATION_KEY, false)
        Log.e("pref", "O valor da configuração é: $pref")

        readTasks()
    }

    override fun onStop() {
        super.onStop()

        Log.e("lifecycle", "Main onStop")
    }

    override fun onPause() {
        super.onPause()

        Log.e("lifecycle", "Main onPause")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("lifecycle", "Main onDestroy")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, PreferenceFragment::class.java))
            R.id.action_logout -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initComponents() {
        tasksAdapter = TasksAdapter(this, binding.tvMessage, object : TaskItemClickListener {
            override fun onClick(task: Task) {
                binding.etTitle?.let {
                    binding.etTitle?.setText(task.title)
                } ?: run {
                    val intent = Intent(this@MainActivity, TaskFormActivity::class.java)
                    intent.putExtra("task", task)
                    startActivity(intent)
                }
            }

            override fun onMarkAsCompleteClick(position: Int, task: Task) {
                taskService.markAsCompleted(task).observe(this@MainActivity) { responseDto ->
                    if (responseDto.isError) {
                        Toast.makeText(this@MainActivity, "Falha na operação", Toast.LENGTH_SHORT).show()
                    } else {
                        responseDto.value?.let { value ->
                            tasksAdapter.updateItem(position, value)
                        }
                    }
                }
            }

            override fun onShareClick(task: Task) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, task.title)
                intent.setType("text/plain")

                startActivity(
                    Intent.createChooser(
                        intent,
                        ContextCompat.getString(this@MainActivity, R.string.share_using)
                    )
                )
            }
        })
        binding.rvTasks.adapter = tasksAdapter
        binding.rvTasks.layoutManager = LinearLayoutManager(this)

        ItemTouchHelper(TaskItemTouchCallback(object : TaskItemSwipeListener {
            override fun onSwipe(position: Int) {
                val task = tasksAdapter.getItem(position)
                taskService.delete(task).observe(this@MainActivity) { responseDto ->
                    if (responseDto.isError) {
                        tasksAdapter.refreshItem(position)
                    } else {
                        tasksAdapter.deleteItem(position)
                    }
                }
            }
        })).attachToRecyclerView(binding.rvTasks)

        binding.srlTasks.setOnRefreshListener {
            readTasks()
        }

        binding.fabNewTask.setOnClickListener {
            startActivity(Intent(this, TaskFormActivity::class.java))

//            Log.e("thread", "Thread 1: ${Thread.currentThread().name}")
//
//            CoroutineScope(Dispatchers.Main).launch {
//                Log.e("thread", "Thread 2: ${Thread.currentThread().name}")
//                Thread.sleep(10000)
//
//                withContext(Dispatchers.Main) {
//                    Log.e("thread", "Thread 3: ${Thread.currentThread().name}")
//                    binding.tvMessage.setText("Finalizou")
//                }
//            }
        }
    }

    private fun readTasks() {
        taskService.readAll().observe(this) { responseDto ->
            binding.srlTasks.isRefreshing = false

            if (responseDto.isError) {
                Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                binding.tvMessage.text = ContextCompat.getString(this, R.string.get_tasks_error)
            } else {
                responseDto.value?.let { tasks ->
                    tasksAdapter.setItems(tasks)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    AlertDialog.Builder(this)
                            .setMessage(R.string.notification_permission_rationale)
                            .setPositiveButton(
                                R.string.accept
                            ) { _, _ ->
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }.setNegativeButton(R.string.not_accept, null)
                            .show()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.e("permission", "Permission dada: $isGranted")
        }
}