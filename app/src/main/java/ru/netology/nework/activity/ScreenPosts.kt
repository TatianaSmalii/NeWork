package ru.netology.nework.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.activity.AppActivity.Companion.idArg
import ru.netology.nework.adapter.AdapterScreenPosts
import ru.netology.nework.adapter.OnIteractionListener
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.ScreenPostsBinding
import ru.netology.nework.dialog.DialogAuth
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.AuthViewModel.Companion.DIALOG_IN
import ru.netology.nework.viewmodel.AuthViewModel.Companion.myID
import ru.netology.nework.viewmodel.AuthViewModel.Companion.userAuth
import ru.netology.nework.viewmodel.PostsViewModel
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint

class ScreenPosts : Fragment() {
    val viewModel: PostsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val binding = ScreenPostsBinding.inflate(inflater, container, false)

        binding.bottomNavigation.selectedItemId = R.id.screenPosts

        fun showBar(txt: String) {
            Snackbar.make(
                binding.root,
                txt,
                Snackbar.LENGTH_LONG
            ).show()
        }

        val adapter = AdapterScreenPosts(object : OnIteractionListener {
            override fun onLike(post: Post) {
                if (userAuth) {
                   viewModel.like(post, !post.likedByMe)
                } else {
                    DialogAuth.newInstance(
                        DIALOG_IN,
                        "Для установки лайков нужна авторизация, выполнить вход?"
                    )
                        .show(childFragmentManager, "TAG")
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, "Share Post")
                startActivity(shareIntent)

            }

            override fun onEdit(post: Post) {

            }

            override fun onRemove(post: Post) {
                if (post.authorId == myID)
                    viewModel.removePost(post)
                //viewModel.loadPosts()
            }

            override fun openLinkVideo(post: Post) {

            }

            override fun openCardPost(post: Post) {
                findNavController().navigate(
                    R.id.postView,
                    Bundle().apply {
                        idArg = post.id
                    }
                )
            }
        })
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val state = adapter.currentList.size < posts.size
            adapter.submitList(posts)
            if (state) binding.list.smoothScrollToPosition(0)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
            if (state.errorNetWork) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }

            if (state.error403) {
                userAuth = false
                myID = null

                showBar("Ошибка авторизации, выполните вход")
            }
            if (state.error415) {
                showBar("Неправильный формат файла!")
            }

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPosts()
        }
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_red_light,
        )

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_posts -> {
                    println("click POSTS")
                    viewModel.loadPosts()
                    true
                }

                R.id.menu_events -> {
                    println("click EVENTS")
                    true
                }

                R.id.menu_users -> {
                    findNavController().navigate(
                        R.id.screenUsers,
                    )
                    true
                }

                else -> false
            }

        }

        binding.fab.setOnClickListener {
            if (userAuth) {
                findNavController().navigate(
                    R.id.newPostFrag
                )
            } else {
                DialogAuth.newInstance(
                    DIALOG_IN,
                    "Для добавления поста нужно авторизоваться, выполнить вход?"
                )
                    .show(childFragmentManager, "TAG")
            }

        }

        return binding.root

    }

    override fun onStart() {
        viewModel.loadPosts()
        super.onStart()
    }
}