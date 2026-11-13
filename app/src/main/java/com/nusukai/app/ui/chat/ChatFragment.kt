package com.nusukai.app.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nusukai.app.R
import com.nusukai.app.data.ChatError
import com.nusukai.app.data.ChatMessage
import com.nusukai.app.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ChatAdapter(onCopy = ::copy, onShare = ::share)
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        binding.rvMessages.adapter = adapter

        binding.btnSend.setOnClickListener { submit() }
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) { submit(); true } else false
        }
        binding.btnRetry.setOnClickListener { viewModel.retry() }
        binding.chipSuggest1.setOnClickListener { viewModel.send(getString(R.string.chat_suggest_1)) }
        binding.chipSuggest2.setOnClickListener { viewModel.send(getString(R.string.chat_suggest_2)) }
        binding.chipSuggest3.setOnClickListener { viewModel.send(getString(R.string.chat_suggest_3)) }
        binding.btnClear.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.chat_clear_confirm)
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.clear() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun submit() {
        val text = binding.etMessage.text?.toString().orEmpty()
        if (text.isBlank()) return
        viewModel.send(text)
        binding.etMessage.setText("")
    }

    private fun render(state: ChatUiState) {
        adapter.submitList(state.messages) {
            if (state.messages.isNotEmpty()) binding.rvMessages.scrollToPosition(state.messages.size - 1)
        }
        binding.layoutEmpty.visibility = if (state.messages.isEmpty()) View.VISIBLE else View.GONE
        binding.progress.visibility = if (state.loading) View.VISIBLE else View.INVISIBLE
        binding.tvTyping.visibility = if (state.loading) View.VISIBLE else View.GONE
        binding.btnSend.isEnabled = !state.loading
        binding.tvDemoBadge.visibility = if (state.demo) View.VISIBLE else View.GONE

        val error = state.error
        binding.layoutError.visibility = if (error == null) View.GONE else View.VISIBLE
        if (error != null) {
            binding.tvError.setText(
                when (error) {
                    ChatError.NETWORK -> R.string.chat_error_network
                    ChatError.RATE -> R.string.chat_error_rate
                    ChatError.SERVER -> R.string.chat_error_server
                }
            )
        }
    }

    private fun copy(message: ChatMessage) {
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Nusuk AI", message.content))
        Snackbar.make(binding.root, R.string.chat_copied, Snackbar.LENGTH_SHORT).show()
    }

    private fun share(message: ChatMessage) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message.content)
        }
        startActivity(Intent.createChooser(send, getString(R.string.chat_share_chooser)))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
