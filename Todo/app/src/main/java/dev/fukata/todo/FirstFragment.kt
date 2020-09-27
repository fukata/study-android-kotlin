package dev.fukata.todo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.fukata.todo.db.AppDatabase
import dev.fukata.todo.db.Todo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    val todos: ArrayList<Todo> = ArrayList()
    lateinit var todosView: RecyclerView
    lateinit var viewAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.refreshTodos()

        val viewManager = LinearLayoutManager(context)
        this.viewAdapter = TodoAdapter(this.todos)
        this.todosView = view.findViewById<RecyclerView>(R.id.todos).apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun refreshTodos() {
        GlobalScope.launch {
            val todoList = AppDatabase.getInstance(requireContext()).todoDao().getAll()
            todos.clear()
            todos.addAll(todoList)
            Handler(Looper.getMainLooper()).postDelayed({
                viewAdapter.notifyDataSetChanged()
            }, 0)
        }
    }
}

class TodoAdapter(private val todos: ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    class TodoViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val textView = TextView(parent.context)
        return TodoViewHolder(textView)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.textView.text = this.todos[position].title
        holder.textView.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                GlobalScope.launch {
                    AppDatabase.getInstance(holder.itemView.context).todoDao().delete(todos[position])
                    todos.remove(todos[position])
                    Handler(Looper.getMainLooper()).postDelayed({
                        notifyDataSetChanged()
                    }, 0)
                }
                return true
            }
        })
    }

    override fun getItemCount(): Int {
        return this.todos.size
    }
}

