package com.jayce.vexis.business.role.manage

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.creezen.commontool.Config.PreferenceParam.AVATAR_SAVE_TIME
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.AndroidTool
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.R
import com.jayce.vexis.business.role.dashboard.AvatarSignnature
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.domain.bean.ActiveEntry
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.Util.request

class ActiveDataActivity : BaseActivity<ActivityActiveDataBinding>() {

    private var activeBean: ActiveBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activeEntry = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("activeItem", ActiveEntry::class.java)
        } else {
            intent.getParcelableExtra("activeItem")
        }
        activeBean = activeEntry?.unParcelable() ?: return
        initPage()
    }

    private fun initPage() {
        with(binding) {
            activeBean?.let { bean ->
                AndroidTool.getDataAsync(AVATAR_SAVE_TIME, 0L) {
                    ThreadTool.ui {
                        NetTool.setImage(
                            this@ActiveDataActivity,
                            avata,
                            "${BASE_FILE_PATH}head/${bean.userID}.png",
                            placeHolder = ContextCompat.getDrawable(this@ActiveDataActivity, R.drawable.default_head),
                            key = AvatarSignnature("key:$it"),
                            isCircle = true
                        )
                    }

                }

                nickname.text = bean.nickname
                userID.text = bean.userID
                createTime.text = bean.createTime
                support.cardText = "${bean.support}"
                against.cardText = "${bean.against}"
                inform.cardText = "${bean.inform}"
                reported.cardText = "${bean.reported}"
                follow.cardText = "${bean.follow}"
                fans.cardText = "${bean.fans}"
                post.cardText = "${bean.post}"
                setAdministrator.setOnClickListener {
                    activeBean?.userID?.let {
                        manageUser(1, it)
                    }
                }
                delete.setOnClickListener {
                    activeBean?.userID?.let {
                        manageUser(2, it)
                    }
                }
            }
        }
    }

    private fun manageUser(operation: Int, userId: String) {
        activeBean?.let {
            ThreadTool.runOnMulti {
                request<UserService, LinkedTreeMap<String, Boolean>>({
                    manageUser(operation, userId)
                }) {
                    val result = it["operationResult"]
                    if (result == true) finish()
                }
            }
        }
    }
}