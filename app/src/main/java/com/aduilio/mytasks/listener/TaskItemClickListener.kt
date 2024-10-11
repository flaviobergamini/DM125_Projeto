package com.aduilio.mytasks.listener

import com.aduilio.mytasks.entity.Task

interface TaskItemClickListener {

    fun onClick(task: Task)

    fun onMarkAsCompleteClick(position: Int, task: Task)

    fun onShareClick(task: Task)
}