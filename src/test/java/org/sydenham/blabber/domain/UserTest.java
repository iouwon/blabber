package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    public static final String MSG = "howdy";
    public static final String DAVE = "dave";
    public static final String JANINE = "janine";
    private final User user = User.from(DAVE);

    @Test
    public void userPostsMessageIsRecordedInTimeline() {
        User userWithOneMessage = user.postMessage(MSG);

        assertThat(user, equalTo(userWithOneMessage));
        assertThat(user.timeline.length(), equalTo(0));
        assertThat(userWithOneMessage.timeline.length(), equalTo(1));

        Stream<Post> postStream = userWithOneMessage.timeline.posts().stream();

        assertThat(postStream.anyMatch((post) -> post.user.name.equals(DAVE) && post.message.equals(MSG)), is(true));
    }

    @Test
    public void usersAreEqual() {
        User otherUser = User.from(DAVE).postMessage(MSG);

        assertThat(user, equalTo(otherUser));
    }

    @Test
    public void userAFollowsUserBSeesOwnAndUserBsMessages() {
        User followed = User.from(JANINE);
        User follower = user.follow(followed);
        LinkedList<Post> wallPosts = new LinkedList<>();

        follower.wall.forEach(wallPosts::add);
        assertThat(wallPosts.isEmpty(), is(true));

        followed = followed.postMessage(MSG);
        follower = follower.postMessage(MSG);

        follower = follower.harvest(new HashSet<>(asList(followed, follower)));
        follower.wall.forEach(wallPosts::add);
        assertThat(wallPosts.size(), is(2));
        Stream<Post> postStream = wallPosts.stream();
        assertThat(postStream.anyMatch((post) -> post.user.name.equals(JANINE) && post.message.equals(MSG)), is(true));
        postStream = wallPosts.stream();
        assertThat(postStream.anyMatch((post) -> post.user.name.equals(DAVE) && post.message.equals(MSG)), is(true));
    }
}
