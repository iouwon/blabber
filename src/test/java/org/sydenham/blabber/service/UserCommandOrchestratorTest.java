package org.sydenham.blabber.service;

import org.sydenham.blabber.domain.Post;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserCommandOrchestratorTest {

    private static final String MSG = "what lovely weather we're having... not!";
    private static final String CAROL = "carol";
    private static final String BRIAN = "brian";

    private final UserCommandOrchestrator userCommandOrchestrator = UserCommandOrchestrator.newObj();

    @Test
    public void unknownUserIsCreatedOnPosting() {
        UserCommandOrchestrator newUserCommandOrchestrator = this.userCommandOrchestrator.postMessage(CAROL, MSG);

        LinkedList<Post> carolsPosts = new LinkedList<>();

        newUserCommandOrchestrator.forEachTimelinePostOf(CAROL, carolsPosts::add);

        Stream<Post> postStream = carolsPosts.stream();

        assertThat(carolsPosts.size(), equalTo(1));
        assertThat(postStream.anyMatch(post -> post.user.name.equals(CAROL) && post.message.equals(MSG)), is(true));
    }

    @Test
    public void knownUserHasPostAddedToTimeline() {
        UserCommandOrchestrator newUserCommandOrchestrator = this.userCommandOrchestrator.postMessage(CAROL, MSG);

        newUserCommandOrchestrator = newUserCommandOrchestrator.postMessage(CAROL, MSG);

        LinkedList<Post> carolsPosts = new LinkedList<>();

        newUserCommandOrchestrator.forEachTimelinePostOf(CAROL, carolsPosts::add);

        Stream<Post> postStream = carolsPosts.stream();
        assertThat(carolsPosts.size(), equalTo(2));

        Stream<Post> allCarolsMatchingPosts = postStream.filter(post -> post.user.name.equals(CAROL) && post.message.equals(MSG));
        assertThat(allCarolsMatchingPosts.count(), is(2l));
    }

    @Test
    public void followingAnotherUserAddsTheirPostsToYourWall() {
        UserCommandOrchestrator newUserCommandOrchestrator = userCommandOrchestrator.userAFollowsUserB(CAROL, BRIAN);

        String msgFromCarolToBrian = "hi brian";
        String msgFromBrianToCarol = "hello carol";

        newUserCommandOrchestrator = newUserCommandOrchestrator.postMessage(CAROL, msgFromCarolToBrian).postMessage(BRIAN, msgFromBrianToCarol);

        LinkedList<Post> carolsWallPosts = new LinkedList<>();

        newUserCommandOrchestrator.forEachWallPostOf(CAROL, carolsWallPosts::addLast);

        assertThat(carolsWallPosts.size(), equalTo(2));

        Stream<Post> postStream = carolsWallPosts.stream();
        Stream<Post> carolsMatchingPosts = postStream.filter(post -> post.user.name.equals(CAROL) && post.message.equals(msgFromCarolToBrian));
        assertThat(carolsMatchingPosts.count(), equalTo(1l));

        postStream = carolsWallPosts.stream();
        Stream<Post> briansMatchingPosts = postStream.filter(post -> post.user.name.equals(BRIAN) && post.message.equals(msgFromBrianToCarol));
        assertThat(briansMatchingPosts.count(), equalTo(1l));

        Post firstPost = carolsWallPosts.pop();
        Post secondPost = carolsWallPosts.pop();
        assertThat(secondPost.wasPostedBefore(firstPost), is(false));
    }
}