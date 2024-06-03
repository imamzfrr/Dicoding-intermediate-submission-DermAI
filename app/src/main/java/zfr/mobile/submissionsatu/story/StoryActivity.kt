package zfr.mobile.submissionsatu.story


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zfr.mobile.submissionsatu.SessionManager
import zfr.mobile.submissionsatu.addstory.AddStoryActivity
import zfr.mobile.submissionsatu.api.ApiClient
import zfr.mobile.submissionsatu.databinding.ActivityStoryBinding
import zfr.mobile.submissionsatu.detail.DetailActivity
import zfr.mobile.submissionsatu.maps.MapsActivity

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var adapter: StoryAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: StoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val authToken = sessionManager.getAuthToken()
        if (authToken.isNullOrEmpty()) {
            redirectToLogin()
            return
        }

        repository = StoryRepository(ApiClient.getApiService(authToken))

        setupRecyclerView()

        val viewModelFactory = StoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StoryViewModel::class.java)

        lifecycleScope.launch {
            viewModel.stories.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        adapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.recyclerViewStories.visibility = if (isListEmpty) View.GONE else View.VISIBLE
            binding.tvEmpty.visibility = if (isListEmpty) View.VISIBLE else View.GONE
            binding.recyclerViewStories.visibility = if (loadState.source.refresh is LoadState.NotLoading) View.VISIBLE else View.GONE
            binding.progressBar.visibility = if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE
            binding.tvError.visibility = if (loadState.source.refresh is LoadState.Error) View.VISIBLE else View.GONE
        }

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.mapsButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter { storyItem ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY_ITEM, storyItem)
            startActivity(intent)
        }
        binding.recyclerViewStories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewStories.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { adapter.retry() },
            footer = LoadingStateAdapter { adapter.retry() }
        )
    }

    private fun redirectToLogin() {
        sessionManager.clearAuthToken()
        sessionManager.redirectToWelcome()
        finish()
    }
}