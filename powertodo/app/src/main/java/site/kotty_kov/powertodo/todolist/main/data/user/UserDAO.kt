package site.kotty_kov.powertodo.todolist.main.data.user

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM userinfo  WHERE id == 1")
    suspend fun getUserObject(): UserInfo

    @Query("SELECT * FROM userinfo  WHERE id == 1")
    fun getUserInfo(): LiveData<UserInfo>


    @Update
    fun update(i: UserInfo)

    @Insert
    fun insert(i: UserInfo)
}