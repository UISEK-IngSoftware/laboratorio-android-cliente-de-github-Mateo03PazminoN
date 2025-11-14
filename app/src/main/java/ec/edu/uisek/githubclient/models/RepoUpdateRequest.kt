package ec.edu.uisek.githubclient.models

data class RepoUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val homepage: String? = null,
    val visibility: String? = null, // "public" | "private" | null
    val default_branch: String? = null
)
