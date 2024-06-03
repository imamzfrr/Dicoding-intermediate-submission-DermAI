package zfr.mobile.submissionsatu.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import zfr.mobile.submissionsatu.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY_ITEM = "extra_story_item"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyItem = intent.getParcelableExtra<StoryItem>(EXTRA_STORY_ITEM)
        if (storyItem != null) {
            setupViews(storyItem)
        } else {
            finish()
        }
    }

    private fun setupViews(storyItem: StoryItem) {
        binding.tvDetailName.text = storyItem.name
        binding.tvDetailDescription.text = storyItem.description
        Glide.with(this)
            .load(storyItem.photoUrl)
            .into(binding.ivDetailPhoto)
    }
}

