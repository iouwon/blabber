package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    public static final String MSG = "howdy";
    public static final String NAME = "dave";
    private User user = new User(NAME);

    @Test
    public void userPostsMessageIsRecordedInTimeline() {
        User updatedTimelineUser = user.postMessage(MSG);

        assertThat(user, not(equalTo(updatedTimelineUser)));
        assertThat(user.timeline.length(), equalTo(0));
        assertThat(updatedTimelineUser.timeline.length(), equalTo(1));

        Stream<Post> postStream = updatedTimelineUser.timeline.posts().stream();

        assertThat(postStream.anyMatch((post) -> post.user.name.equals(NAME) && post.message.equals(MSG)), is(true));
    }

    @Test
    public void usersAreEqual() {
        User otherUser = new User(NAME);

        assertThat(user, equalTo(otherUser));
    }
}
