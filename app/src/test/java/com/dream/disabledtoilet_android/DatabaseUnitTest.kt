package com.dream.disabledtoilet_android

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase.ToiletDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel

@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {

    private lateinit var database: ToiletDatabase

    private val exampleToilet = ToiletModel(
        distance = 100.5,
        number = 1,
        basis = "기본 정보",
        restroom_name = "중앙 공원 화장실",
        address_road = "서울특별시 강남구 테헤란로 123",
        address_lot = "서울특별시 강남구 삼성동 456-78",
        male_toilet_count = 3,
        male_urinal_count = 5,
        male_child_toilet_count = 1,
        male_child_urinal_count = 2,
        female_toilet_count = 4,
        female_child_toilet_count = 1,
        management_agency_name = "강남구청",
        waste_disposal_method = "정화조",
        safety_management_facility_installed = "설치됨",
        emergency_bell_installed = "설치됨",
        diaper_change_table_available = "가능",
        diaper_change_table_location = "여성 화장실 내부",
        data_reference_date = "2025-01-01",
        opening_hours_detail = "24시간 운영",
        opening_hours = "24시간",
        installation_date = "2020-01-01",
        phone_number = "02-123-4567",
        remodeling_date = "2024-01-01",
        wgs84_latitude = 37.5665,
        wgs84_longitude = 126.9780,
        male_disabled_toilet_count = 1,
        male_disabled_urinal_count = 1,
        female_disabled_toilet_count = 1,
        emergency_bell_location = "입구 근처",
        restroom_entrance_cctv_installed = "설치됨",
        category = "공공 화장실",
        restroom_ownership_type = "공공",
        save = listOf("user1", "user2", "user3")
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            klass = ToiletDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun storeToilet() = runBlocking {
        val dao = database.databaseDao()
        dao.insertToilet(exampleToilet)
        val storedToilets = dao.getAllToilets()
        println("Stored Toilets: $storedToilets")
    }

    @Test
    fun deleteToilet() = runBlocking {
        val dao = database.databaseDao()
        dao.insertToilet(exampleToilet)
        dao.deleteToilet(exampleToilet)
        val storedToilets = dao.getAllToilets()
        println("Remaining Toilets after deletion: $storedToilets")
    }
}
