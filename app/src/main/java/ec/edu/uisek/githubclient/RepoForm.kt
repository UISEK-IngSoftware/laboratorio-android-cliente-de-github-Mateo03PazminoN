package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.models.RepoUpdateRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Detectar si es edición
        val isEditing = intent.getBooleanExtra("isEditing", false)
        val owner = intent.getStringExtra("owner") ?: ""
        val oldRepoName = intent.getStringExtra("repoName") ?: ""

        if (isEditing) {
            binding.repoNameInput.setText(oldRepoName)
            binding.repoDescriptionInput.setText(intent.getStringExtra("repoDescription") ?: "")
        }

        binding.cancelButton.setOnClickListener { finish() }

        // Guardar: crear o editar según corresponda
        binding.saveButton.setOnClickListener {
            if (isEditing) {
                updateRepo(owner, oldRepoName)
            } else {
                createRepo()
            }
        }
    }

    // ------------------------------------------------------------
    // VALIDAR FORMULARIO
    // ------------------------------------------------------------
    private fun validateForm(): Boolean {
        val repoName = binding.repoNameInput.text.toString()

        if (repoName.isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }

        if (repoName.contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }

        binding.repoNameInput.error = null
        return true
    }

    // ------------------------------------------------------------
    // CREAR REPOSITORIO (POST)
    // ------------------------------------------------------------
    private fun createRepo() {
        if (!validateForm()) return

        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()

        val repoRequest = RepoRequest(repoName, repoDescription)
        val apiService = RetrofitClient.getApiService()
        val call = apiService.addRepo(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val errorMsg = "Error al crear el repositorio: ${t.message}"
                Log.e("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    // ------------------------------------------------------------
    // EDITAR REPOSITORIO (PATCH)
    // ------------------------------------------------------------
    private fun updateRepo(owner: String, oldRepoName: String) {
        if (!validateForm()) return

        val newRepoName = binding.repoNameInput.text.toString().trim()
        val newRepoDescription = binding.repoDescriptionInput.text.toString().trim()

        val updateRequest = RepoUpdateRequest(
            name = newRepoName,
            description = newRepoDescription
        )

        val api = RetrofitClient.getApiService()
        val call = api.updateRepo(
            owner = owner,
            repoName = oldRepoName,
            repoUpdateRequest = updateRequest
        )

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado correctamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val errorMsg = "Error al actualizar el repositorio: ${t.message}"
                Log.e("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    // ------------------------------------------------------------
    // UTILIDAD
    // ------------------------------------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
