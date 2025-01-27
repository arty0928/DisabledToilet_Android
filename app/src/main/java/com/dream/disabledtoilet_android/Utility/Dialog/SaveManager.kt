//package com.dream.disabledtoilet_android.Utility.Dialog
//
//import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel
//import android.content.Context
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import com.dream.disabledtoilet_android.R
//import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
//import com.dream.disabledtoilet_android.User.User
//import com.dream.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class SaveManager(private val context: Context) {
//
//    private val TAG = "saveManager"
//    private val firestore = FirebaseFirestore.getInstance()
//    var loadingDialog = LoadingDialog()
//
//    // 아이콘 토글 함수 (BottomSheet)
//    fun toggleIcon(view: View, toilet: com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel) {
//        val imageView1: ImageView = view.findViewById(R.id.save_icon1)
//        val imageView2: ImageView = view.findViewById(R.id.save_icon2)
//        val saveCount : TextView = view.findViewById(R.id.toilet_save_count)
//
//        val currentSrc1 = imageView1.drawable
//        val currentSrc2 = imageView2.drawable
//
//        if (currentSrc1.constantState == context.getDrawable(R.drawable.save_icon)?.constantState || currentSrc2.constantState == context.getDrawable(R.drawable.save_icon)?.constantState) {
//            imageView1.setImageResource(R.drawable.saved_star_icon)
//            imageView2.setImageResource(R.drawable.saved_star_icon)
//            updateSave(toilet)
////            toilet.save += ToiletData.getCurretnUser()
//
//        } else {
//            imageView1.setImageResource(R.drawable.save_icon)
//            imageView2.setImageResource(R.drawable.save_icon)
////            toilet.save -= 1
//            //삭제
////            updateSave(toilet)
//
//            //삭제
//
//        }
//
//        saveCount.text = "저장 (${toilet.save})"
//
//        // Firebase에서 해당 toilet 업데이트
//        updateToiletInFirebase(toilet)
//        Log.d(TAG, "Updated save count: ${toilet.save}")
//    }
//
//    fun updateSave(toilet: com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel){
//        val db = FirebaseFirestore.getInstance()
//        val currentSave = getCurrentSave(toilet)
//
//        //toilet.save 업데이트
//        val newSaveValue = toilet.save + getCurrentSave(toilet)
//
//        //toilet.number로 문서찾기
//        db.collection("dreamhyoja")
//            .document(toilet.number.toString())
//            .update("save", newSaveValue)
//            .addOnSuccessListener {
//                Log.d(TAG, "save update successfully")
//            }
//            .addOnFailureListener { e->
//                Log.d(TAG,"Error updating save")
//            }
//    }
//
//    /**
//     * 현재 User가 해당 화장실을 좋아요 했는지 체크
//     * 1 : 추가 , -1 : 삭제
//     */
//    fun getCurrentSave(toilet: com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel) : Int {
//        var likedToilets = ToiletData.currentUser?.likedToilets
//
//        if (likedToilets != null) {
//            //좋아요한 화장실 중에
//            for (t in likedToilets){
//                //있다면 1 반환
//                if(toilet == t){
//                    return 1
//                }
//            }
//            return -1
//        }
//        return -1
//    }
//
//    // 아이콘 토글 함수 (Expanded BottomSheet)
//    fun toggleIcon2(view: View, toilet: com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel){
//        val imageView : ImageView = view.findViewById(R.id.icon_toggle)
//        val saveCount : TextView = view.findViewById(R.id.toilet_save_count)
//
//        val src = imageView.drawable
//        if(src.constantState == context.getDrawable(R.drawable.save_icon)?.constantState){
//            imageView.setImageResource(R.drawable.saved_star_icon)
//            toilet.save +=1
//        }
//        else{
//            imageView.setImageResource(R.drawable.save_icon)
////            toilet.save -= curr
//        }
//
//        saveCount.text = "저장 (${toilet.save})"
//        // Firebase에서 해당 toilet 업데이트
//        updateToiletInFirebase(toilet)
//        Log.d(TAG, "Updated save count: ${toilet.save}")
//    }
//
//
//    private fun updateToiletInFirebase(toilet: com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel) {
//        val toiletRef = firestore.collection("toilets").document(toilet.number.toString()) // toilet의 number를 사용하여 문서 참조
//
//        toiletRef.update("save", toilet.save)
//            .addOnSuccessListener {
//                Log.d(TAG, "Toilet save updated successfully: ${toilet.number}")
//
//                // 비동기 처리로 ToiletData.initialize 호출
//                CoroutineScope(Dispatchers.IO).launch {
//                    val initResult = ToiletData.initialize()
//                    withContext(Dispatchers.Main) {
//                        if (initResult) {
//                            Log.d(TAG, "Toilet data loaded successfully.")
//                            loadingDialog.dismiss()
//                        } else {
//                            Log.e(TAG, "Failed to load toilet data.")
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating toilet save: ", e)
//            }
//    }
//
//}