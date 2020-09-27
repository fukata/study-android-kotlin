package dev.fukata.todo.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.*

@Entity(tableName = "todos")
data class Todo (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title", defaultValue = "")
    val title: String,
    @ColumnInfo(name = "memo", defaultValue = "")
    val memo: String
)