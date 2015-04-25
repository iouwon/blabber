package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostTest {

    public static final String NAME = "dave";
    public static final String MSG = "msg";

    @Test
    public void fromReturnsNewPostWithParametersAndCurrentTimestamp() {
        Post newPost = Post.from(new User(NAME), MSG);

        assertThat(newPost.user.name, is(NAME));
        assertThat(newPost.message, is(MSG));
        assertThat(newPost.timestamp.compareTo(LocalDateTime.now().plusMinutes(1)), is(-1));
        assertThat(newPost.timestamp.compareTo(LocalDateTime.now().minusMinutes(1)), is(1));
    }

    @Test
    public void wasPostedBeforeIsTrueIfPostedBeforeAndFalseIfPostedAfter() throws InterruptedException {
        Post post1 = Post.from(new User(NAME), MSG);
        contriveTimestampeDifference();
        Post post2 = Post.from(new User(NAME), MSG);

        assertThat(post1.wasPostedBefore(post2), is(true));
        assertThat(post2.wasPostedBefore(post1), is(false));
    }

    @Test
    public void wasPostedBeforeIsFalseIfPostedAtSameTime() throws InterruptedException {
        LocalDateTime timestamp = LocalDateTime.now();
        Post post1 = new Post(new User(NAME), MSG, timestamp);
        Post post2 = new Post(new User(NAME), MSG, timestamp);

        assertThat(post1.wasPostedBefore(post2), is(false));
        assertThat(post2.wasPostedBefore(post1), is(false));
    }

    private void contriveTimestampeDifference() throws InterruptedException {
        Thread.sleep(1);
    }
}
