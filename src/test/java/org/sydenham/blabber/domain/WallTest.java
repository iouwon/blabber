package org.sydenham.blabber.domain;

import org.testng.annotations.Test;

import java.util.LinkedList;

public class WallTest {

    public static final String NAME = "dave";
    public static final String MSG = "howdy";
    private Wall wall = new Wall();

    @Test
    public void followingUserMergesTheirTimelineOntoTheWallInPostTimeOrder() {
        User user = new User(NAME);

        user.postMessage(MSG);

        Wall wall = this.wall.follow(user);

        LinkedList<Post> postsOnWall = new LinkedList<>();

        wall.forEach(postsOnWall::add);

        postsOnWall.stream().anyMatch((post) -> post.user.name.equals(NAME) && post.message.equals(MSG));
    }
}
