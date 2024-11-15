package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.JobDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.UserResponseEntity
import ru.netology.nework.entity.toDto
import ru.netology.nework.entity.toEntity
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.ApiError403
import ru.netology.nework.error.ApiError404
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val daoUser: UserDao,
    private val daoJob: JobDao,
) : UsersRepository {

    private val _allUsersJob = daoJob.getJobs().map(List<JobEntity>::toDto)
    override val allUsersJob: Flow<List<Job>>
        get() = _allUsersJob


    private val _allUsers = daoUser.getAllUsers().map(List<UserResponseEntity>::toDto)
    override val allUsers: Flow<List<UserResponse>>
        get() = _allUsers

    //    private var _userJob = MutableLiveData<List<Job>>()
//    override val userJob: Flow<List<Job>>
//        get() = _userJob.asFlow()

//    private var _userJob = MutableStateFlow<List<Job>>(emptyList<Job>())
//    override val userJob: Flow<List<Job>>
//        get() = _userJob

    override suspend fun getUsers() {
        try {
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                when (response.code()) {
                    403 -> throw ApiError403(response.code().toString())
                    else -> throw ApiError(response.code(), response.message())
                }
            }
            val users = response.body() ?: throw ApiError(response.code(), response.message())
            daoUser.insertAllUser(
                users.toEntity()
            )
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError403) {
            throw ApiError403("403")
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun getUser(id: Long): UserResponse {
        try {
            val response = apiService.getUser(id)
            if (!response.isSuccessful) {
                when (response.code()) {
                    404 -> throw ApiError404(response.code().toString())
                    else -> throw ApiError(response.code(), response.message())
                }
            }

            val user = response.body() ?: throw ApiError(response.code(), response.message())
            daoUser.insertUser(UserResponseEntity.fromDto(user))
            return user

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError404) {
            throw ApiError404("404")
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getJobs(id: Long) {
        println("getJobs $id")
        try {
            val response = apiService.getUserIdJobs(id)
            val jobs = response.body() ?: throw ApiError(response.code(), response.message())
            val _jobs = jobs.map { job -> job.copy(idUser = id) }
            daoJob.insertAllJob(
                _jobs.toEntity()
            )

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override suspend fun getJobsBD(id: Long) {
//        try {
//            daoJob.getAllJobs(id).map(List<JobEntity>::toDto).flowOn(Dispatchers.Default)
//                .collect { job ->
//                    job.forEach { println(it) }
//                    _userJob.update { job }
//                }
//        } catch (e: Exception) {
//            println("EXCEPTION BASE DATA")
//        }
//    }
}