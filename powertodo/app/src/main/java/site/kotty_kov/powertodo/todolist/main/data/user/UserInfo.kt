package site.kotty_kov.powertodo.todolist.main.data.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userinfo")
data class UserInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 1,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var description: String? = "",
    var password: String,
    var lastAppState: Int
) {
    suspend fun getUserInfo(): UserInfo {
        TODO("Not yet implemented")
    }
}

