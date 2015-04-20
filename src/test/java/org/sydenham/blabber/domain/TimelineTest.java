package org.sydenham.blabber.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

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
}
