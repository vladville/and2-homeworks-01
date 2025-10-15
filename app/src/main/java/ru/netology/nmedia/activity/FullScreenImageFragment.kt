package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.FeedFragment.Companion.textArgs
import ru.netology.nmedia.databinding.FragmentFullImageBinding
import ru.netology.nmedia.dto.load

class FullScreenImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFullImageBinding.inflate(
            inflater,
            container,
            false,
        )

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fullScreenImageView.setOnClickListener {
            findNavController().popBackStack()
        }

        val imageUrl = arguments?.textArgs
        binding.fullScreenImageView.load("http://10.0.2.2:9999/media/" + imageUrl, 10)

        return binding.root
    }
}