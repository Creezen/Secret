package com.jayce.vexis.business.role.manage

import android.os.Bundle
import com.creezen.commontool.Config.AVATAR_SAVE_TIME
import com.creezen.commontool.Config.EVENT_TYPE_FEEDBACK
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.AndroidTool.getDataAsync
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.sendChatMessage
import com.creezen.tool.NetTool.sendManagerMessage
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.getScope
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.domain.bean.ActiveEntry
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ability.EventRepository.Companion.SCOPE_EVENT

class ActiveDataActivity : BaseActivity<ActivityActiveDataBinding>() {

    private var activeBean: ActiveBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activeEntry = intent.getParcelableExtra("activeItem", ActiveEntry::class.java)
        activeBean = activeEntry?.unParcelable() ?: return
    }

    override fun onResume() {
        super.onResume()
        val bean = activeBean ?: return
        initPage(bean)
    }

    private fun initPage(bean: ActiveBean) = binding.apply {
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
            request<UserService, _>({ setUserAsAdmin(bean.userID) }) { result ->
                result.onTrue {
                    sendMessage("用户\"${bean.nickname}\"已成为管理员！")
                    "设置用户成功".toast()
                    finish()
                }.onFalse {
                    return@onFalse
                }

            }
        }
        delete.setOnClickListener {
            request<UserService, _>({ deleteUser(bean.userID) }) { result ->
                result.onTrue {
                    sendMessage("删除用户 ${bean.nickname}")
                    "删除用户成功".toast()
                    finish()
                }.onFalse {
                    return@onFalse
                }
            }
        }

        getDataAsync(AVATAR_SAVE_TIME, 0L) {
            ThreadTool.ui {
                val url = "${bean.userID}.png"
                val placeHolder = R.drawable.default_head
                avata.load(url, placeHolder, it.toString(), true)
            }
        }
    }

    private fun sendMessage(content: String) {
        val scope = getScope(SCOPE_EVENT) ?: return
        sendManagerMessage(scope, content)
    }
}