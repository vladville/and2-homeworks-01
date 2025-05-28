package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.numbersToThousands
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.data.observe(this) { post ->

            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
                likeCount.text = numbersToThousands(post.likes)
                shareCount.text = numbersToThousands(post.shares)
                ViewCount.text = numbersToThousands(post.views)

                likeIcon.setImageResource(
                    if (post.likeByMe) {
                        R.drawable.ic_liked
                    } else {
                        R.drawable.ic_likes
                    }
                )
            }
        }

        binding.likeIcon.setOnClickListener {
            viewModel.like()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.share()
        }

    }
}