package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubAPIService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        // FAB para crear nuevo repositorio
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    // ------------------------------------------------------------
    // CONFIGURAR RECYCLER VIEW
    // ------------------------------------------------------------
    private fun setupRecyclerView() {

        reposAdapter = ReposAdapter(
            onEditClick = { repo ->
                openEditForm(repo)
            },
            onDeleteClick = { repo ->
                deleteRepo(repo)
            }
        )

        binding.reposRecyclerView.adapter = reposAdapter
    }

    // ------------------------------------------------------------
    // OBTENER LISTA DE REPOS
    // ------------------------------------------------------------
    private fun fetchRepositories() {
        val apiService: GithubAPIService = RetrofitClient.githubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
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

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("Error al cargar repos: ${t.message}")
            }
        })
    }

    // ------------------------------------------------------------
    // ABRIR FORMULARIO PARA CREAR REPO
    // ------------------------------------------------------------
    private fun displayNewRepoForm() {
        val intent = Intent(this, RepoForm::class.java)
        startActivity(intent)
    }

    // ------------------------------------------------------------
    // ABRIR FORMULARIO PARA EDITAR REPO
    // ------------------------------------------------------------
    private fun openEditForm(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java)

        intent.putExtra("isEditing", true)
        intent.putExtra("repoName", repo.name)
        intent.putExtra("repoDescription", repo.description ?: "")
        intent.putExtra("owner", repo.owner.login)

        startActivity(intent)
    }

    // ------------------------------------------------------------
    // ELIMINAR REPO (DELETE)
    // ------------------------------------------------------------
    private fun deleteRepo(repo: Repo) {
        val api = RetrofitClient.githubApiService

        val call = api.deleteRepo(
            owner = repo.owner.login,
            repoName = repo.name
        )

        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado correctamente")
                    fetchRepositories() // refrescar lista
                } else {
                    showMessage("Error eliminando repo: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexión: ${t.message}")
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
