package com.example.simpletodo;

import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;

/* Inner class that defines the table contents */
public abstract class TodoItemContract implements BaseColumns {
    public static final String TABLE_NAME = "todo";
    public static final String COLUMN_NAME_TEXT = "textTodo";
    public static final String COLUMN_NAME_LONGTEXT = "longTextTodo";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_FLAG = "flag";
    public static final String COLUMN_NAME_CHECKED = "checked";
    
    public static final String AUTHORITY = "com.example.simpletodo";
    public static final String CONTENT_TODO = "todoList";
    
    public static final Uri TODO_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).path(CONTENT_TODO).build();
    

    public static final int TODO_FLAG_NORMAL = 0;
    public static final int TODO_FLAG_IMPORTANT = 1;
    public static final int TODO_FLAG_CRITICAL = 2;    

}