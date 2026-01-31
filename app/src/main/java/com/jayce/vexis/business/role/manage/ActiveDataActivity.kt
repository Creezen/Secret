package com.jayce.vexis.business.role.manage

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.creezen.commontool.Config.PreferenceParam.AVATAR_SAVE_TIME
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.AndroidTool
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.R
import com.jayce.vexis.business.role.dashboard.AvatarSignnature
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.ActiveEntry
import com.jayce.vexis.foundation.route.UserService

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
            activeBean?.let {
                val avatarTimestamp = AndroidTool.readPrefs {
                    getLong(AVATAR_SAVE_TIME, 0)
                }
                NetTool.setImage(
                    this@ActiveDataActivity,
                    avata,
                    "${BASE_FILE_PATH}head/${it.userID}.png",
                    placeHolder = ContextCompat.getDrawable(this@ActiveDataActivity, R.drawable.default_head),
                    key = AvatarSignnature("key:$avatarTimestamp"),
                    isCircle = true
                )

                nickname.text = it.nickname
                userID.text = it.userID
                createTime.text = it.createTime
                support.cardText = "${it.support}"
                against.cardText = "${it.against}"
                inform.cardText = "${it.inform}"
                reported.cardText = "${it.reported}"
                follow.cardText = "${it.follow}"
                fans.cardText = "${it.fans}"
                post.cardText = "${it.post}"
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