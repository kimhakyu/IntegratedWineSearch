package com.example.integratedwinesearch.chat

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.integratedwinesearch.R
import com.example.integratedwinesearch.chat.model.ChatMessage
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        initChatList()
        initSendButton()
    }

    private fun initChatList() {
        chatMessages.add(
            ChatMessage(
                id = "1",
                message = "안녕하세요! 와인과 안주 추천을 도와드리는 AI 챗봇입니다. 어떤 와인이나 안주를 찾고 계신가요?",
                time = "오후 2:30",
                isUser = false
            )
        )

        chatMessages.add(
            ChatMessage(
                id = "2",
                message = "스테이크랑 잘 어울리는 와인 추천해주세요!",
                time = "오후 2:31",
                isUser = true
            )
        )

        chatMessages.add(
            ChatMessage(
                id = "3",
                message = "스테이크와 잘 어울리는 와인을 찾고 계신가요? 카베네 소비뇽이나 말벡을 추천드립니다. 풍부한 타닌과 깊은 맛이 육류와 완벽한 조화를 이룹니다.",
                time = "오후 2:31",
                isUser = false
            )
        )

        chatMessages.add(
            ChatMessage(
                id = "4",
                message = "가격대는 어느 정도가 좋을까요?",
                time = "오후 2:32",
                isUser = true
            )
        )

        chatMessages.add(
            ChatMessage(
                id = "5",
                message = "초보자에게는 칠레산 카베네 소비뇽이나 뉴질랜드 소비뇽 블랑을 추천합니다. 가격 대비 품질이 좋아 부담 없이 즐기기 좋습니다.",
                time = "오후 2:32",
                isUser = false
            )
        )

        chatAdapter = ChatAdapter(chatMessages)

        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.scrollToPosition(chatMessages.lastIndex)
    }

    private fun initSendButton() {
        btnSend.setOnClickListener {
            val input = etMessage.text.toString().trim()
            if (input.isEmpty()) return@setOnClickListener

            val userMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                message = input,
                time = getCurrentTime(),
                isUser = true
            )

            chatAdapter.addMessage(userMessage)
            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            etMessage.text.clear()

            // 나중에 여기서 AI 호출 후 응답 addMessage 하면 됨
        }
    }

    private fun getCurrentTime(): String {
        val now = Date()
        val amPm = if (DateFormat.format("a", now).toString().lowercase(Locale.getDefault()).contains("am")) {
            "오전"
        } else {
            "오후"
        }
        val time = DateFormat.format("h:mm", now).toString()
        return "$amPm $time"
    }
}