package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoViewModel : ViewModel() {

    private val todoApiService = TodosService.api

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private var nextTodoId = 4

    // Sync state management with StateFlow
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    init {
        _todos.value = listOf(
            Todo(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
            Todo(id = 2, title = "Review MVVM architecture patterns", completed = true),
            Todo(id = 3, title = "Build todo list app", completed = false)
        )

        Log.d("TodoViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addTodo(title: String) {
        val newTodo = Todo(id = nextTodoId++, title = title, completed = false)
        _todos.value = _todos.value + newTodo
    }

    fun toggleCompleted(todoId: Int) {
        _todos.value = _todos.value.map { todo ->
            if (todo.id == todoId) todo.copy(completed = !todo.completed) else todo
        }
    }

    fun removeTodo(todoId: Int) {
        _todos.value = _todos.value.filter { it.id != todoId }
    }

    fun syncTodos() {
        viewModelScope.launch {
            try {
                _syncState.value = SyncState.Loading

                val todoDtos = withContext(Dispatchers.IO) {
                    todoApiService.getTodos()
                }

                val todos = todoDtos.take(20).map { dto ->
                    Todo(
                        id = dto.id,
                        title = dto.title,
                        completed = dto.completed
                    )
                }

                _todos.value = todos
                nextTodoId = todos.maxOfOrNull { it.id }?.plus(1) ?: 1
                _syncState.value = SyncState.Success

            }catch (e: Exception){
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TodoViewModel", "ViewModel cleared")
    }
}