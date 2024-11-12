package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var loadingDialog = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        // 비동기로 초기화 및 로드 수행
        CoroutineScope(Dispatchers.IO).launch {
            // 초기화 작업 (예: 데이터 로드)
            val initResult = initializeApp()

            withContext(Dispatchers.Main) {
                loadingDialog.dismiss() // 로딩 다이얼로그 종료
                if (initResult) {
                    // 로그인 상태 확인
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        // 로그인 상태일 경우 MainActivity로 이동
                        startActivity(Intent(this@StartActivity, MainActivity::class.java))
                    } else {
                        // 비로그인 상태일 경우 NonloginActivity로 이동
                        startActivity(Intent(this@StartActivity, NonloginActivity::class.java))
                    }
                    finish() // 현재 액티비티 종료
                } else {
                    // 초기화 실패 시 에러 처리 (필요시 추가)
                }
            }
        }
    }

    private suspend fun initializeApp(): Boolean {
        // 초기화 작업 수행 (예: 데이터 로드)
        // 성공 시 true 반환, 실패 시 false 반환
        return try {
            // 예시: ToiletData.initialize() 호출
            ToiletData.initialize()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
