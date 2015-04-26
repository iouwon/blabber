package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimelineTest {

    public static final String USERNAME_1 = "user1";
    public static final String USERNAME_2 = "user2";
    public static final String MESSAGE_1 = "message1";
    public static final String MESSAGE_2 = "message2";
    public static final LocalDateTime posted = LocalDateTime.MIN;
    private final Timeline timeline = new Timeline();

    @Test
    public void addingPostAppendsToANewTimeline() {
        Post newPost = new Post(User.from(USERNAME_1), MESSAGE_1, posted);
        Post expectedPost = new Post(User.from(USERNAME_1), MESSAGE_1, posted);

        Timeline newTimeline = timeline.append(newPost);

        assertThat(timeline.length(), equalTo(0));
        assertThat(newTimeline.length(), equalTo(1));
        assertThat(newTimeline.posts().contains(expectedPost), is(true));
    }

    @Test
    public void addingMultiplePostsAppendsAllToANewTimeline() {
        Post newPost1 = new Post(User.from(USERNAME_1), MESSAGE_1, posted);
        Post newPost2 = new Post(User.from(USERNAME_2), MESSAGE_2, posted);
        Post expectedPost1 = new Post(User.from(USERNAME_1), MESSAGE_1, posted);
        Post expectedPost2 = new Post(User.from(USERNAME_2), MESSAGE_2, posted);

        Timeline newTimeline = timeline.append(newPost1).append(newPost2);

        assertThat(timeline.length(), equalTo(0));
        assertThat(newTimeline.length(), equalTo(2));
        assertThat(newTimeline.posts().contains(expectedPost1), is(true));
        assertThat(newTimeline.posts().contains(expectedPost2), is(true));
    }

    @Test
    public void timelinesAreEqual() {
        Post newPost1 = new Post(User.from(USERNAME_1), MESSAGE_1, posted);
        Post newPost2 = new Post(User.from(USERNAME_2), MESSAGE_2, posted);

        Timeline thisTimeline = timeline.append(newPost1).append(newPost2);
        Timeline thatTimeline = new Timeline().append(newPost1).append(newPost2);

        assertThat(thisTimeline, equalTo(thatTimeline));
    }

    @Test
    public void timelinesAreNotEqual() {
        Post newPost1 = new Post(User.from(USERNAME_1), MESSAGE_1, posted);
        Post newPost2 = new Post(User.from(USERNAME_2), MESSAGE_2, posted);

        Timeline thisTimeline = timeline.append(newPost1);
        Timeline thatTimeline = new Timeline().append(newPost2);

        assertThat(thisTimeline, not(equalTo(thatTimeline)));
    }
}
