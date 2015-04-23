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
}
