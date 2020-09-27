package dev.fukata.todo.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAll(): List<Todo>

    @Insert
    suspend fun insertAll(vararg todos: Todo)

    @Delete
    fun delete(todo: Todo)
}
