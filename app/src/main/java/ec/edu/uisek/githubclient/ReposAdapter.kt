package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter(
    private val onEditClick: (Repo) -> Unit,
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.Adapter<ReposAdapter.ReposViewHolder>() {

    private var repoList: List<Repo> = emptyList()

    inner class ReposViewHolder(
        private val binding: FragmentRepoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.repoName.text = repo.name
            binding.repoDescription.text = repo.description ?: "(Sin descripción)"
            binding.repoLang.text = repo.languaje ?: "N/A"

            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .circleCrop()
                .into(binding.repoOwnerImagen)

            // Botón Editar
            binding.editButton.setOnClickListener { onEditClick(repo) }

            // Botón Eliminar
            binding.deleteButton.setOnClickListener { onDeleteClick(repo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repoList[position])
    }

    override fun getItemCount(): Int = repoList.size

    fun updateRepositories(newList: List<Repo>) {
        repoList = newList
        notifyDataSetChanged()
    }
}