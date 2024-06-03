package zfr.mobile.submissionsatu

import zfr.mobile.submissionsatu.api.response.story.StoryItem

object DataDummy {
    fun generateDummyStories(): List<StoryItem> {
        val stories = mutableListOf<StoryItem>()
        for (i in 0..10) {
            val story = StoryItem(
                id = i.toString(),
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2022-01-01T00:00:00Z"
            )
            stories.add(story)
        }
        return stories
    }
}

