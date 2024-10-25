package com.aduilio.mytasks.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.TaskListItemBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.TaskItemClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskViewHolder(
    private val context: Context,
    private val binding: TaskListItemBinding,
    private val listener: TaskItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun setValues(task: Task) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val useExpandedFormat = sharedPreferences.getBoolean("expanded_date", false)

        binding.tvTitle.text = task.title

        binding.tvDate.text = task.date?.let { date ->
            if (useExpandedFormat) {
                date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))
            } else {
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        } ?: run {
            "-"
        }

        binding.tvTime.text = task.time?.let {
            task.time.toString()
        } ?: run {
            "-"
        }

        val currentDate = LocalDate.now()

        task.date?.let { date ->
            when {
                task.completed -> binding.statusIndicator.setBackgroundResource(R.color.green_700)
                date == null -> binding.statusIndicator.setBackgroundResource(R.color.blue_700)
                date < currentDate -> binding.statusIndicator.setBackgroundResource(R.color.red_500)
                date == currentDate -> binding.statusIndicator.setBackgroundResource(R.color.yellow_500)
                else -> binding.statusIndicator.setBackgroundResource(R.color.blue_700)
            }
        } ?: run {
            if (!task.completed)
                binding.statusIndicator.setBackgroundResource(R.color.blue_700)
            else
                binding.statusIndicator.setBackgroundResource(R.color.green_700)
        }

        binding.root.setOnClickListener {
            listener.onClick(task)
        }

        binding.root.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(ContextCompat.getString(context, R.string.mark_as_completed))
                .setOnMenuItemClickListener {
                listener.onMarkAsCompleteClick(adapterPosition, task)
                true
            }
            menu.add(ContextCompat.getString(context, R.string.share))
                .setOnMenuItemClickListener {
                listener.onShareClick(task)
                true
            }
        }
    }
}
