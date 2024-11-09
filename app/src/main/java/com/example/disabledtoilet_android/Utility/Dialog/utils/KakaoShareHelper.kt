package com.example.disabledtoilet_android.Utility.Dialog.utils

import ToiletModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

class KakaoShareHelper(private val context: Context) {

    // 카카오 맵 공유 함수
    fun shareKakaoMap(toilet: ToiletModel) {
        val toiletAddress = toilet.address_road ?: ""
        val toiletLatitude = toilet.wgs84_latitude
        val toiletLongitude = toilet.wgs84_longitude

        val kakaoMapWebUrl = "https://map.kakao.com/link/map/${toiletLatitude},${toiletLongitude}"
        val kakaoMapAppUrl = "kakaomap://look?p=${toiletLatitude},${toiletLongitude}"
        val kakaoMapRouteWebUrl = "https://map.kakao.com/link/to/${toiletAddress},${toiletLatitude},${toiletLongitude}"

        val defaultFeed = FeedTemplate(
            content = Content(
                title = "방광곡곡 - 화장실 위치",
                description = toiletAddress,
                imageUrl = "https://mud-kage.kakao.com/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                link = Link(
                    webUrl = kakaoMapWebUrl,
                    mobileWebUrl = kakaoMapWebUrl
                )
            ),
            buttons = listOf(
                Button(
                    "위치 보기",
                    Link(
                        webUrl = kakaoMapWebUrl,
                        mobileWebUrl = kakaoMapAppUrl
                    )
                ),
                Button(
                    "길찾기",
                    Link(
                        webUrl = kakaoMapRouteWebUrl,
                        mobileWebUrl = kakaoMapRouteWebUrl
                    )
                )
            )
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareDefault(context, defaultFeed) { sharingResult, error ->
                if (error != null) {
                    Log.e("KakaoShareHelper", "카카오톡 공유 실패", error)
                    Toast.makeText(context, "카카오톡 공유에 실패했습니다.", Toast.LENGTH_SHORT).show()
                } else if (sharingResult != null) {
                    context.startActivity(sharingResult.intent)
                }
            }
        } else {
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultFeed)
            try {
                openWithDefault(context, sharerUrl)
            } catch (e: Exception) {
                Toast.makeText(context, "공유에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 기본 브라우저로 URL 열기 함수
    private fun openWithDefault(context: Context, url: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "URL을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}