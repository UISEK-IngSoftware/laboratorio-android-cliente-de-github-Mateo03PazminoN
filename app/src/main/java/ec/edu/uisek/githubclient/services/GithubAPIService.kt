package ec.edu.uisek.githubclient.services;

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.GET

interface GithubAPIService {
    //api.github.com/user/repo
    @GET ("user/repos")
    fun getRepos() : Call<List<Repo>>
}