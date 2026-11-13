package com.nusukai.app.ui.profile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nusukai.app.MainActivity
import com.nusukai.app.R
import com.nusukai.app.databinding.FragmentProfileBinding
import com.nusukai.app.util.LocaleHelper
import com.nusukai.app.util.UiHelpers
import com.nusukai.app.util.nusuk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.Date

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // The system photo picker needs no storage permission.
    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) saveAvatar(uri)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val app = requireContext().nusuk
        val settings = app.settings

        binding.etName.setText(settings.userName)
        showName()
        showAvatar()

        binding.btnSaveName.setOnClickListener {
            settings.userName = binding.etName.text?.toString().orEmpty().trim()
            showName()
            Snackbar.make(binding.root, R.string.profile_saved, Snackbar.LENGTH_SHORT).show()
        }
        binding.btnChangePhoto.setOnClickListener { pickPhoto.launch("image/*") }
        binding.ivAvatar.setOnClickListener { pickPhoto.launch("image/*") }
        binding.btnOpenSettings.setOnClickListener { (requireActivity() as MainActivity).openSettings() }

        UiHelpers.bindLanguageToggle(
            binding.toggleLang, binding.btnLangBn, binding.btnLangEn, binding.btnLangAr, settings
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val done = app.quizRepository.doneTopics().size
            val total = app.content.topics.size
            val history = app.quizRepository.history(5)
            if (_binding == null) return@launch

            binding.progressLearning.max = maxOf(total, 1)
            binding.progressLearning.progress = done
            binding.tvProgressValue.text = getString(R.string.profile_progress_value, done, total)

            binding.tvHistoryEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
            val df = DateFormat.getDateInstance(DateFormat.MEDIUM, LocaleHelper.locale(settings.language))
            history.forEach { r ->
                binding.layoutHistory.addView(TextView(requireContext()).apply {
                    text = getString(R.string.quiz_history_row, df.format(Date(r.takenAt)), r.correct, r.total, r.percent)
                    setTextColor(ContextCompat.getColor(context, R.color.nusuk_text))
                    setPadding(0, 8, 0, 8)
                })
            }
        }
    }

    private fun showName() {
        binding.tvName.text = requireContext().nusuk.settings.userName
    }

    private fun showAvatar() {
        val path = requireContext().nusuk.settings.avatarPath
        val bitmap = if (path.isNotEmpty() && File(path).exists()) BitmapFactory.decodeFile(path) else null
        if (bitmap != null) {
            binding.ivAvatar.imageTintList = null
            binding.ivAvatar.setImageBitmap(bitmap)
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_person_large)
            binding.ivAvatar.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.brand_gold_light))
        }
    }

    /** Copies the chosen image into the app's private storage, scaled down to at most ~512 px. */
    private fun saveAvatar(uri: Uri) {
        val appContext = requireContext().applicationContext
        viewLifecycleOwner.lifecycleScope.launch {
            val path = withContext(Dispatchers.IO) { copyScaled(appContext, uri) }
            if (path != null) appContext.nusuk.settings.avatarPath = path
            if (_binding != null) showAvatar()
        }
    }

    private fun copyScaled(context: Context, uri: Uri): String? = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= 512 && bounds.outHeight / (sample * 2) >= 512) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (bitmap == null) {
            null
        } else {
            val file = File(context.filesDir, "avatar.jpg")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 88, it) }
            file.absolutePath
        }
    } catch (e: Exception) {
        null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
