package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.numbersToThousands

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессией будущего",
            content = "Привет. Это новая Нетология. Когда-то Нетология начиналась с интенсив...",
            published = "26 мая в 19:13",
        )

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likeCount.text = numbersToThousands(post.likes)
            shareCount.text = numbersToThousands(post.shares)
            ViewCount.text = numbersToThousands(post.views)

            if (post.likeByMe) {
                likeIcon.setImageResource(R.drawable.ic_liked)
            }

            likeIcon?.setOnClickListener {
                post.likeByMe = !post.likeByMe

                likeIcon.setImageResource(
                    if (post.likeByMe) {
                        post.likes++
                        R.drawable.ic_liked
                    } else {
                        post.likes--
                        R.drawable.ic_likes
                    }
                )
                likeCount.text = numbersToThousands(post.likes)
            }

            shareIcon?.setOnClickListener {
                post.shares++
                shareCount.text = numbersToThousands(post.shares)
            }

        }
    }
}