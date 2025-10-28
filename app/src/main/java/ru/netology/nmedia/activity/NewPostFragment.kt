package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArgs
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
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

        val viewModel: PostViewModel by viewModels()

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(
                        requireContext(),
                        R.string.image_picker_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }

                val uri = result.data?.data ?: return@registerForActivityResult

                viewModel.updatePhoto(uri, uri.toFile())
            }

        //check is it new post?
        val isNewPost = viewModel.isNewPost()
        val pref = context?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        if (isNewPost) {
            pref?.getString(CONTENT_KEY, null)?.let { text ->
                binding.edit.setText(text)
            }
        }

        arguments?.textArgs?.let(binding.edit::setText)
        binding.edit.requestFocus()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        viewModel.removePhoto() //fix
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }

                    else -> false
                }

        }, viewLifecycleOwner)

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo == null) {
                binding.previewContainer.isGone = true
                return@observe
            }

            binding.preview.setImageURI(photo.uri)
            binding.previewContainer.isVisible = true

        }
        binding.remove.setOnClickListener {
            viewModel.removePhoto()
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .createIntent {
                    photoLauncher.launch(it)
                }
        }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .createIntent {
                    photoLauncher.launch(it)
                }
        }


        viewModel.postCreated.observe(viewLifecycleOwner) {
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