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
    private val chatRepository = ChatRepository()

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
            requestBotReply(input)
        }
    }

    private fun requestBotReply(input: String) {
        btnSend.isEnabled = false

        val loadingId = "loading_${System.currentTimeMillis()}"
        val loadingMessage = ChatMessage(
            id = loadingId,
            message = "답변 생성 중입니다...",
            time = getCurrentTime(),
            isUser = false
        )
        chatAdapter.addMessage(loadingMessage)
        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)

        chatRepository.sendMessage(input) { success, reply, error ->
            if (!isAdded) return@sendMessage
            requireActivity().runOnUiThread {
                btnSend.isEnabled = true
                if (success) {
                    chatAdapter.updateMessage(loadingId, reply.orEmpty())
                } else {
                    chatAdapter.updateMessage(
                        loadingId,
                        error ?: "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    )
                }
                chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
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
