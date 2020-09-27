package dev.fukata.todo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import dev.fukata.todo.db.AppDatabase
import dev.fukata.todo.db.Todo
import kotlinx.android.synthetic.main.create_todo_dialog.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateTodoDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.create_todo_dialog, null)
            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Create",
                    DialogInterface.OnClickListener { dialog, id ->
                        val titleView = view.findViewById<EditText>(R.id.title)
                        Log.d(tag, "positive. text=${titleView.editableText}")
                        val todo = Todo(title = titleView.editableText.toString(), memo = "")
                        Log.d(tag, todo.toString())
                        GlobalScope.launch {
                            AppDatabase.getInstance(requireContext()).todoDao().insertAll(todo)
                        }
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(tag, "cancel")
                        getDialog()?.cancel()
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    suspend fun createTodo(todo: Todo) {

    }
}