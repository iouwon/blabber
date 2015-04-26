package org.sydenham.blabber.domain;

import java.util.HashSet;
import java.util.Objects;

public class User {

    public final String name;
    public final Timeline timeline;
    public final Wall wall;

    private User(String name) {
        this.name = name;
        this.timeline = new Timeline();
        this.wall = new Wall().follow(this);
    }

    private User(String name, Timeline timeline, Wall wall) {
        this.name = name;
        this.timeline = (Timeline) timeline.clone();
        this.wall = (Wall) wall.clone();
    }

    public static User from(String name) {
        return new User(name);
    }

    public User postMessage(String msg) {
        return new User(name, ((Timeline) timeline.clone()).append(Post.from(this, msg)), wall);
    }

    public User follow(User followed) {
        return new User(name, timeline, wall.follow(followed));
    }

    @SuppressWarnings("unchecked")
    public User harvest(HashSet<User> theMaybeFollowed) {
        HashSet<User> theMaybeFollowedAndMe = (HashSet<User>) theMaybeFollowed.clone();
        theMaybeFollowedAndMe.add(this);
        return new User(name, timeline, wall.harvest(theMaybeFollowedAndMe));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
