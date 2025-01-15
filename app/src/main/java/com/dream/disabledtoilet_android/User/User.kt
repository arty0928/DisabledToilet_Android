import androidx.lifecycle.MutableLiveData

data class User(
    val email: String = "",
    val name: String = "",
    val photoURL: String = "",
    // 해당 화장실 번호만
    var likedToilets: List<String> = emptyList(),
    var registedToilets: List<String> = emptyList()
)
