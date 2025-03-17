package com.example.note_app

import androidx.compose.material.icons.Icons



import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NotesDatabase(context: Context) : SQLiteOpenHelper(context, "notes.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getAllNotes(): List<String> {
        val notes = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM notes", null)
        while (cursor.moveToNext()) {
            notes.add(cursor.getString(cursor.getColumnIndexOrThrow("content")))
        }
        cursor.close()
        return notes
    }

    fun addNote(note: String) {
        writableDatabase.execSQL("INSERT INTO notes (content) VALUES (?)", arrayOf(note))
    }

    fun deleteNote(note: String) {
        writableDatabase.execSQL("DELETE FROM notes WHERE content = ?", arrayOf(note))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = NotesDatabase(this)
        setContent {
            NotesApp(db)
        }
    }
}

@Composable
fun NotesApp(db: NotesDatabase) {
    var notes by remember { mutableStateOf(db.getAllNotes()) }
    var newNote by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Android Notes", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = newNote,
            onValueChange = { newNote = it },
            label = { Text("New Note") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                if (newNote.isNotBlank()) {
                    db.addNote(newNote)
                    notes = db.getAllNotes()
                    newNote = ""
                }
            }) {
                Text("Add Note")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        notes.forEach { note ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(note, fontSize = 18.sp)
                IconButton(onClick = {
                    db.deleteNote(note)
                    notes = db.getAllNotes()
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesApp(NotesDatabase(LocalContext.current))
}

