package com.jayce.vexis.business.role.manage

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.creezen.commontool.Config.PreferenceParam.AVATAR_SAVE_TIME
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.AndroidTool
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
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
    }

    override fun onResume() {
        super.onResume()
        val bean = activeBean ?: return
        initPage(bean)
    }

    private fun initPage(bean: ActiveBean) {
        with(binding) {
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

            setAdministrator.setOnClickListener { manageUser(1, bean.userID) }
            delete.setOnClickListener { manageUser(2, bean.userID) }

            AndroidTool.getDataAsync(AVATAR_SAVE_TIME, 0L) {
                ThreadTool.ui {
                    NetTool.setImage(
                        this@ActiveDataActivity,
                        avata,
                        "${bean.userID}.png",
                        R.drawable.default_head,
                        it.toString(),
                        true
                    )
                }
            }
        }
    }

    private fun manageUser(operation: Int, userId: String) {
        request<UserService, Boolean>({ manageUser(operation, userId) }) { result ->
            if (!result) return@request
            "操作成功".toast()
            finish()
        }
    }
}