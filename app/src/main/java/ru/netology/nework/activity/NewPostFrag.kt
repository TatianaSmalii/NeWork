package ru.netology.nework.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.NewPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.error.UnknownError
import ru.netology.nework.media.MediaUpload
import ru.netology.nework.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nework.viewmodel.PostsViewModel
import java.io.InputStream

const val MAX_SIZE_FILE = 15_728_640L

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint


class NewPostFrag : Fragment() {
    private var upload: MediaUpload? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = NewPostBinding.inflate(layoutInflater)
        val viewModel: PostsViewModel by viewModels()
        var typeAttach: AttachmentType? = null
        var lastStateLoading = false

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        if (!binding.content.text.isNullOrBlank()) {
                            val txt = binding.content.text.toString()
                            val post = Post(id = 0, authorId = 0, content = txt)
                            viewModel.savePost(post, upload, typeAttach)
                        } else {
                            context?.toast("Для создания поста нужен контент!")
                        }
                        true
                    }

                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }

                    else -> false
                }

        }, viewLifecycleOwner)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
//                    println("resultCode ${it.resultCode}")
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                Activity.RESULT_OK -> {
                    val uri: Uri? = it.data?.data
                    when (typeAttach) {
                        AttachmentType.IMAGE -> {
                            val file = uri?.toFile()
//                            file?.let {
//                                println("file.name ${file.name}  file.length() ${file.length()} typeAttach $typeAttach")
//                            }
                            if (file?.length()!! < MAX_SIZE_FILE) {
                                viewModel.changePhoto(uri, file)
                                upload = MediaUpload(file)
                                binding.btnClear.visibility = View.VISIBLE
                            } else {
                                context?.toast("Размер вложения превышает максимально допустимый 15Мб!")
                            }
                        }
                        AttachmentType.AUDIO -> {
                        }
                        AttachmentType.VIDEO -> {
                        }
                        else -> {}
                    }
                }

                Activity.RESULT_CANCELED -> {
                    upload = null
                }
            }
        }

        binding.bottomNavigationNewPost.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_pic -> {
                    typeAttach = AttachmentType.IMAGE
                    ImagePicker.with(this)
                        .galleryOnly()
                        .crop()
                        .maxResultSize(2048, 2048)
                        .createIntent(launcher::launch)
                    true
                }

                R.id.add_geo -> {
                    println("click GEO")
                    true
                }

                R.id.add_users -> {
                    println("click USERS")
                    true
                }

                R.id.photo -> {
                    typeAttach = AttachmentType.IMAGE
                    ImagePicker.with(this)
                        .cameraOnly()
                        .crop()
                        .maxResultSize(2048, 2048)
                        .createIntent(launcher::launch)
                    true
                }

                R.id.add_file -> {
                    binding.selectAttach.visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }

        with(binding) {
            attachAudio.setOnClickListener {
                typeAttach = AttachmentType.AUDIO
                launcher.launch(getIntent())
                selectAttach.visibility = View.GONE
            }
            attachVideo.setOnClickListener {
                typeAttach = AttachmentType.VIDEO
                launcher.launch(getIntent())
                selectAttach.visibility = View.GONE
            }
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it == viewModel.noPhoto) {
                binding.photo.visibility = View.GONE
                binding.content.focusAndShowKeyboard()
                typeAttach = null
                return@observe
            }
            typeAttach = AttachmentType.IMAGE
            binding.content.clearFocus()
            binding.photo.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        viewModel.dataState.observe(viewLifecycleOwner) {
            if (it.loading) binding.btnClear.visibility = View.GONE
            if (!it.loading && lastStateLoading) findNavController().navigateUp()
            binding.progress.isVisible = it.loading
            lastStateLoading = it.loading
        }

        binding.btnClear.setOnClickListener {
            viewModel.clearPhoto()
            it.visibility = View.GONE
            upload = null
        }

        return binding.root
    }

    private var curFrag: CurrentShowFragment? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            curFrag = context as CurrentShowFragment
        } catch (e: ClassCastException) {
            throw UnknownError
        }
    }

    override fun onDetach() {
        super.onDetach()
        curFrag?.getCurFragmentDetach()
        curFrag = null
    }

    override fun onStart() {
        super.onStart()
        curFrag?.getCurFragmentAttach(getString(R.string.new_post))

    }

    private fun getIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*" // That's needed for some reason, crashes otherwise
        putExtra(
            // List all file types you want the user to be able to select
            Intent.EXTRA_MIME_TYPES, arrayOf(
//                "text/html", // .html
//                "text/plain", // .txt
//                "application/pdf",
                "audio/mpeg",
                "video/mp4",
            )
        )
    }
    private fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    //            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "*/*" // That's needed for some reason, crashes otherwise
//                putExtra(
//                    // List all file types you want the user to be able to select
//                    Intent.EXTRA_MIME_TYPES, arrayOf(
//                        "text/html", // .html
//                        "text/plain" // .txt
//                    )
//                )
//                launcher.launch(this)
//            }
}