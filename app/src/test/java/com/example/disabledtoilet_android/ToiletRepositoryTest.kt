package com.example.disabledtoilet_android
import com.example.disabledtoilet_android.ToiletSearch.Toilet
import com.example.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ToiletRepositoryTest {

    @Mock
    private lateinit var database: FirebaseDatabase

    @Mock
    private lateinit var toiletsRef: DatabaseReference

    @Mock
    private lateinit var snapshot: DataSnapshot

    @Mock
    private lateinit var childSnapshot: DataSnapshot

    private lateinit var repository: ToiletRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ToiletRepository().apply {
            this.database = database
            this.toiletsRef = toiletsRef
        }
    }

    @Test
    fun testGetToiletByRoadAddress_Success() {

    }

    @Test
    fun testGetToiletByRoadAddress_NotFound() {

    }

    @Test
    fun testGetToiletByLotAddress_Success() {

    }

    @Test
    fun testGetToiletsByPartialRoadAddress() {

    }
}
