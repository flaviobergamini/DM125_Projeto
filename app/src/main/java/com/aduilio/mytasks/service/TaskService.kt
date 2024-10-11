package com.aduilio.mytasks.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.repository.ResponseDto

class TaskService : ViewModel() {

    private val taskRepository = RetrofitService().getTaskRepository()

    fun save(task: Task): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        task.id?.let { taskId ->
            taskRepository.update(taskId, task).enqueue(MyCallback(taskLiveData))
        } ?: run {
            taskRepository.create(task).enqueue(MyCallback(taskLiveData))
        }

        return taskLiveData
    }

    fun readAll(): LiveData<ResponseDto<List<Task>>> {
        val tasksLiveData = MutableLiveData<ResponseDto<List<Task>>>()

        taskRepository.readAll().enqueue(MyCallback(tasksLiveData))

        return tasksLiveData
    }

    fun delete(task: Task): LiveData<ResponseDto<Void>> {
        val liveData = MutableLiveData<ResponseDto<Void>>()

        task.id?.let { taskId ->
            taskRepository.delete(taskId).enqueue(MyCallback(liveData))
        } ?: run {
            liveData.value = ResponseDto(isError = true)
        }

        return liveData
    }

    fun markAsCompleted(task: Task): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        task.id?.let { taskId ->
            taskRepository.markAsCompleted(taskId).enqueue(MyCallback(taskLiveData))
        } ?: run {
            taskLiveData.value = ResponseDto(isError = true)
        }

        return taskLiveData
    }
}