package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WallTest {

    public static final String USERNAME1 = "dave";
    public static final String USERNAME2 = "fiona";
    public static final String USERNAME3 = "bruce";
    public static final String MSG1 = "msg1";
    public static final String MSG2 = "msg2";
    public static final String MSG3 = "msg3";
    public static final String MSG4 = "msg4";
    public static final String MSG5 = "msg5";
    private final Wall wall = new Wall();

    @Test
    public void followingUserAndCapturingLatestPostsMergesTheirTimelineOntoTheWallInPostTimeOrder() {
        User user = User.from(USERNAME1).postMessage(MSG1).postMessage(MSG2);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        Wall newWall = wall.follow(user).harvest(users);

        LinkedList<Post> postsOnWall = new LinkedList<>();
        newWall.forEach(postsOnWall::addLast);

        Post firstPost = postsOnWall.pop();
        Post secondPost = postsOnWall.pop();
        assertThat(firstPost.user.name, equalTo(USERNAME1));
        assertThat(firstPost.message, equalTo(MSG1));
        assertThat(secondPost.user.name, equalTo(USERNAME1));
        assertThat(secondPost.message, equalTo(MSG2));
        assertThat(secondPost.wasPostedBefore(firstPost), is(false));
        assertThat(postsOnWall.isEmpty(), is(true));
    }

    @Test
    public void followingMultipleUsersAndCapturingLatestPostsMergesTheirTimelinesOntoTheWallInPostTimeOrder() throws InterruptedException {
        User user1 = User.from(USERNAME1).postMessage(MSG1);
        contriveTimestampDifference();
        User user2 = User.from(USERNAME2).postMessage(MSG2).postMessage(MSG3);
        contriveTimestampDifference();
        user1 = user1.postMessage(MSG4);

        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        Wall newWall = wall.follow(user1).follow(user2).harvest(users);

        LinkedList<Post> postsOnWall = new LinkedList<>();
        newWall.forEach(postsOnWall::addLast);

        Post firstPost = postsOnWall.pop();
        Post secondPost = postsOnWall.pop();
        Post thirdPost = postsOnWall.pop();
        Post fourthPost = postsOnWall.pop();

        assertThat(firstPost.user.name, equalTo(USERNAME1));
        assertThat(firstPost.message, equalTo(MSG1));

        assertThat(secondPost.user.name, equalTo(USERNAME2));
        assertThat(secondPost.message, equalTo(MSG2));

        assertThat(thirdPost.user.name, equalTo(USERNAME2));
        assertThat(thirdPost.message, equalTo(MSG3));

        assertThat(fourthPost.user.name, equalTo(USERNAME1));
        assertThat(fourthPost.message, equalTo(MSG4));

        assertThat(secondPost.wasPostedBefore(firstPost), is(false));
        assertThat(thirdPost.wasPostedBefore(secondPost), is(false));
        assertThat(fourthPost.wasPostedBefore(thirdPost), is(false));

        assertThat(postsOnWall.isEmpty(), is(true));
    }

    @Test
    public void followingASubsetOfTheUsersAndCapturingLatestPostsMergesOnlyThoseFollowed() throws InterruptedException {
        User user1 = User.from(USERNAME1).postMessage(MSG1);
        contriveTimestampDifference();
        User user2 = User.from(USERNAME2).postMessage(MSG2).postMessage(MSG3);
        contriveTimestampDifference();
        user1 = user1.postMessage(MSG4);
        User user3 = User.from(USERNAME3).postMessage(MSG5);

        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        Wall newWall = wall.follow(user1).follow(user2).harvest(users);

        LinkedList<Post> postsOnWall = new LinkedList<>();
        newWall.forEach(postsOnWall::addLast);

        Post firstPost = postsOnWall.pop();
        Post secondPost = postsOnWall.pop();
        Post thirdPost = postsOnWall.pop();
        Post fourthPost = postsOnWall.pop();
        assertThat(firstPost.user.name, equalTo(USERNAME1));
        assertThat(firstPost.message, equalTo(MSG1));
        assertThat(secondPost.user.name, equalTo(USERNAME2));
        assertThat(secondPost.message, equalTo(MSG2));
        assertThat(thirdPost.user.name, equalTo(USERNAME2));
        assertThat(thirdPost.message, equalTo(MSG3));
        assertThat(fourthPost.user.name, equalTo(USERNAME1));
        assertThat(fourthPost.message, equalTo(MSG4));
        assertThat(secondPost.wasPostedBefore(firstPost), is(false));
        assertThat(thirdPost.wasPostedBefore(secondPost), is(false));
        assertThat(fourthPost.wasPostedBefore(thirdPost), is(false));
        assertThat(postsOnWall.isEmpty(), is(true));
    }

    private void contriveTimestampDifference() throws InterruptedException {
        Thread.sleep(1);
    }
}
