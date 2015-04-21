package org.sydenham.blabber.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimelineTest {

    private Timeline timeline;
    private LocalDateTime posted = LocalDateTime.MIN;

    @BeforeMethod
    public void setUp() {
        timeline = new Timeline();
    }

    @Test
    public void testAddingPostAppendsToANewTimeline() {
        Post newPost = new Post(new User("user"), "message", posted);
        Post expectedPost = new Post(new User("user"), "message", posted);

        Timeline newTimeline = timeline.append(newPost);

        assertThat(timeline.length(), equalTo(0));
        assertThat(newTimeline.length(), equalTo(1));
        newTimeline.forEach((post) -> assertThat(post, equalTo(expectedPost)));
    }

    @Test
    public void testAddingMultiplePostsAppendsToANewTimeline() {
        String username1 = "user1";
        String username2 = "user2";
        Post newPost1 = new Post(new User(username1), "message1", posted);
        Post newPost2 = new Post(new User(username2), "message2", posted);
        Map<String, Post> expectedPosts = new HashMap<>();
        expectedPosts.put(username1, new Post(new User(username1), "message1", posted));
        expectedPosts.put(username2, new Post(new User(username2), "message2", posted));

        Timeline newTimeline = timeline.append(newPost1).append(newPost2);

        assertThat(timeline.length(), equalTo(0));
        assertThat(newTimeline.length(), equalTo(2));
        newTimeline.forEach((post) -> assertThat(post, equalTo(expectedPosts.get(post.user.name))));
    }
}
