package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.models.RepoUpdateRequest
import retrofit2.Call
import retrofit2.http.*

interface GithubAPIService {

    @GET("user/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>

    @POST("user/repos")
    fun addRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    @PATCH("repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo") repoName: String,
        @Body repoUpdateRequest: RepoUpdateRequest
    ): Call<Repo>


    @DELETE("repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Call<Void>
}
