package com.aduilio.mytasks.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.TaskListItemBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.TaskItemClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskViewHolder(
    private val context: Context,
    private val binding: TaskListItemBinding,
    private val listener: TaskItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun setValues(task: Task) {
        // Obtendo o formato da data a partir das preferências
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val dateFormat = sharedPreferences.getString("date_format", "SHORT") ?: "SHORT"

        // Definindo título
        binding.tvTitle.text = task.title

        // Exibindo data no formato configurado
        binding.tvDate.text = task.date?.let { date ->
            val formatter = if (dateFormat == "LONG") {
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")
            } else {
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            }
            date.format(formatter)
        } ?: "-"

        // Exibindo hora
        binding.tvTime.text = task.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "-"

        // Alterando cor do card com base na data da tarefa
        val currentDate = LocalDate.now()
        val taskDate = task.date

        val cardColor = when {
            taskDate == null || taskDate.isAfter(currentDate) -> R.color.blue_700 // No prazo
            taskDate.isBefore(currentDate.minusDays(1)) -> R.color.red_500 // Vencida
            taskDate.isEqual(currentDate) -> R.color.yellow_500 // Vence hoje
            else -> R.color.blue_700
        }
        binding.root.setCardBackgroundColor(ContextCompat.getColor(context, cardColor))

        // Configurando clique no card
        binding.root.setOnClickListener { listener.onClick(task) }

        // Configurando menu de contexto
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
