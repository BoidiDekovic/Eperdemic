package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Tribu
import com.google.firebase.database.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

@Repository
class TribuDAO {

    @Autowired
    private lateinit var database: DatabaseReference

    fun saveData(tribu: Tribu): Tribu {
        val className = tribu::class.java.simpleName
        val randomId = UUID.randomUUID().toString()
        val key = "${className}_$randomId"
        val dataRef = database.child("/$className/$key")
        dataRef.setValueAsync(tribu).get()
        tribu.id = key
        return tribu
    }

    fun getById(id: String): Tribu?{
        val dataRef = database.child("/Tribu/$id")
        val future = CompletableFuture<Tribu>()

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = dataSnapshot.getValue(Tribu::class.java)
                future.complete(result)
                result.id = id
            }
            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })
        return future.get()
    }

    fun getByName(nombre: String): Tribu? {
        val dataRef = database.child("/Tribu").orderByChild("nombre").equalTo(nombre)
        val future = CompletableFuture<Tribu>()

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val firstTribuEntry = dataSnapshot.children.first()
                    val tribu = firstTribuEntry.getValue(Tribu::class.java)
                    tribu?.id = firstTribuEntry.key
                    future.complete(tribu)
                } else {
                    future.complete(null)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })
        return future.get()
    }

    fun deleteTribuByNombre(nombreDeTribu: String) {
        val tribusRef = database.child("/Tribu").orderByChild("nombre").equalTo(nombreDeTribu)
        val future = CompletableFuture<Void>()

        tribusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val deleteTasks = mutableListOf<CompletableFuture<Void>>()

                    for (tribuDs in dataSnapshot.children) {
                        val deleteTask = CompletableFuture<Void>()
                        tribuDs.ref.removeValue { error, _ ->
                            if (error == null) {
                                deleteTask.complete(null)
                            } else {
                                deleteTask.completeExceptionally(error.toException())
                            }
                        }
                        deleteTasks.add(deleteTask)
                    }

                    CompletableFuture.allOf(*deleteTasks.toTypedArray())
                        .thenAccept { future.complete(null) }
                        .exceptionally { error ->
                            future.completeExceptionally(error.cause ?: error)
                            null
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })

        try {
            future.join()
        } catch (e: CompletionException) {
            throw e.cause ?: e
        }
    }

    fun updateTribu(tribu: Tribu, nombreDeTribu: String) {
        val tribusRef = database.child("/Tribu").orderByChild("nombre").equalTo(tribu.nombre)
        val future = CompletableFuture<Void>()

        tribusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updateTasks = mutableListOf<CompletableFuture<Void>>()

                    for (tribuDs in dataSnapshot.children) {
                        val updateTask = CompletableFuture<Void>()
                        val nuevosDatos = mapOf(
                            "nombre" to nombreDeTribu,
                            "integrantes" to tribu.integrantes,
                            "integranteLider" to tribu.integranteLider
                        )
                        tribuDs.ref.updateChildren(nuevosDatos) { error, _ ->
                            if (error == null) {
                                updateTask.complete(null)
                            } else {
                                updateTask.completeExceptionally(error.toException())
                            }
                        }
                        updateTasks.add(updateTask)
                    }

                    CompletableFuture.allOf(*updateTasks.toTypedArray())
                        .thenAccept { future.complete(null) }
                        .exceptionally { error ->
                            future.completeExceptionally(error.cause ?: error)
                            null
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })

        try {
            future.join()
        } catch (e: CompletionException) {
            throw e.cause ?: e
        }
    }

    fun findAll() :List<Tribu> {
        val tribusRef = database.child("/Tribu")
        val future = CompletableFuture<List<Tribu>>()
        tribusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tribus = mutableListOf<Tribu>()

                for (tribuDs in dataSnapshot.children) {
                    val tribu = tribuDs.getValue(Tribu::class.java)
                    tribu?.let { tribus.add(it) }
                }

                future.complete(tribus)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })

        return future.get()
    }

    fun deleteAll() {
        val rootRef = database.child("/")
        rootRef.removeValueAsync()
            .get()
    }
}