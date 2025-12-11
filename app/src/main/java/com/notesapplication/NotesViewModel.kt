package com.notesapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val dao: NoteDao
) : ViewModel() {

    val notes: StateFlow<List<Note>> =
        dao.getNotes()
            .map { list ->
                list.map { entity ->
                    Note(
                        id = entity.id,
                        title = entity.title,
                        content = entity.content
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            dao.insert(
                NoteEntity(
                    title = title.trim(),
                    content = content.trim()
                )
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.delete(
                NoteEntity(
                    id = note.id,
                    title = note.title,
                    content = note.content
                )
            )
        }
    }
}
