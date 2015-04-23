package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    public static final String MSG = "howdy";
    public static final String NAME = "dave";
    private User user = new User(NAME);

    @Test
    public void userPostsMessageIsRecordedInTimeline() {
        User updatedUser = user.postMessage(MSG);

        assertThat(user.timeline.length(), equalTo(0));
        assertThat(updatedUser.timeline.length(), equalTo(1));

        List<Post> posts = new LinkedList<>();

        updatedUser.timeline.forEach(posts::add);

        assertThat(posts.stream().anyMatch((post) -> post.user.name.equals(NAME) && post.message.equals(MSG)), is(true));
    }
}
