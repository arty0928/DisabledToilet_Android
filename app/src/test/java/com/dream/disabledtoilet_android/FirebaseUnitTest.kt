package com.dream.disabledtoilet_android

import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirebaseUnitTest {
    @Test
    fun getToiletsTest() = runTest{
        ToiletData.initialize()
        for (i in ToiletData.cachedToiletList!!.indices) {
            System.out.println(
                ToiletData.cachedToiletList!![i].toString() + ""
            )
        }
        System.out.println(ToiletData.cachedToiletList)
    }
}