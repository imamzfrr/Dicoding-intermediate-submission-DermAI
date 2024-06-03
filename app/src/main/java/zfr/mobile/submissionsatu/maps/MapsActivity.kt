package zfr.mobile.submissionsatu.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import zfr.mobile.submissionsatu.R
import zfr.mobile.submissionsatu.SessionManager
import zfr.mobile.submissionsatu.api.ApiClient
import zfr.mobile.submissionsatu.databinding.ActivityMapsBinding
import zfr.mobile.submissionsatu.story.StoryRepository

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var repository: StoryRepository
    private lateinit var viewModelFactory: MapsViewModelFactory
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        repository = StoryRepository(ApiClient.getApiService(sessionManager.getAuthToken()!!))
        viewModelFactory = MapsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.storiesWithLocation.observe(this) { stories ->
            stories.forEach { story ->
                val location = story.lat?.let { story.lon?.let { it1 -> LatLng(it, it1) } }
                location?.let {
                    MarkerOptions()
                        .position(it)
                        .title(story.name)
                        .snippet(story.description)
                }?.let {
                    mMap.addMarker(
                        it
                    )
                }
            }
            if (stories.isNotEmpty()) {
                val firstStory = stories[0]
                val firstLocation = firstStory.lat?.let { firstStory.lon?.let { it1 ->
                    LatLng(it,
                        it1
                    )
                } }
                firstLocation?.let { CameraUpdateFactory.newLatLngZoom(it, 10f) }
                    ?.let { mMap.moveCamera(it) }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.fetchStoriesWithLocation()
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}