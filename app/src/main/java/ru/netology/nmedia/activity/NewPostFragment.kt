package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.FeedFragment.Companion.textArgs
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        //check is it new post?
        val isNewPost = arguments?.textArgs.isNullOrEmpty()
        val pref = context?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        if (isNewPost) {
            pref?.getString(CONTENT_KEY, null)?.let { text ->
                binding.edit.setText(text)
            }
        }

        arguments?.textArgs?.let(binding.edit::setText)
        binding.edit.requestFocus()

        binding.save.setOnClickListener {
            if (binding.edit.text.isNotBlank()) {
                val content = binding.edit.text.toString()
                viewModel.changeContent(content)
                viewModel.save()

                //clear pref
                if (isNewPost) {
                    pref?.edit {
                        putString(CONTENT_KEY, "")
                    }
                }
            }

            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (binding.edit.text.isNotBlank() && isNewPost) {
                        val content = binding.edit.text.toString()
                        pref?.edit {
                            putString(CONTENT_KEY, content)
                        }
                    }

                    viewModel.editPostCancel()
                    findNavController().navigateUp()

                }
            }
        )

        return binding.root
    }

    companion object {
        private const val SHARED_PREF_NAME = "repo"
        private const val CONTENT_KEY = "forget_content"
    }
}