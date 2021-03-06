package org.sydenham.blabber.domain;

import org.sydenham.blabber.exception.ApplicationException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Wall implements Cloneable {

    private final HashSet<User> following;

    public Wall() {
        following = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    private Wall(HashSet<User> following) {
        this.following = (HashSet<User>) following.clone();
    }

    @SuppressWarnings("unchecked")
    public Wall follow(User user) {
        HashSet<User> newInterests = (HashSet<User>) following.clone();
        newInterests.add(user);
        return new Wall(newInterests);
    }

    @SuppressWarnings("unchecked")
    public Wall harvest(HashSet<User> users) {
        HashSet<User> usersAndTheirLatestPosts = (HashSet<User>) users.clone();
        usersAndTheirLatestPosts.retainAll(following);
        return new Wall(usersAndTheirLatestPosts);
    }

    public void forEach(Consumer<Post> action) {
        amalgamatedPosts().forEach(action);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ApplicationException(e);
        }
    }

    private List<Post> amalgamatedPosts() {
        LinkedList<Post> amalgamatedPosts = new LinkedList<>();
        LinkedList<LinkedList<Post>> timelines = new LinkedList<>();

        following.forEach(user -> timelines.add(user.timeline.posts()));

        while (timelines.size() > 0) {
            LinkedList<Post> timelineWithEarliestPost = timelineWithEarliestPostIn(timelines.stream());

            if (timelineWithEarliestPost.size() > 0) {
                amalgamatedPosts.addLast(timelineWithEarliestPost.pop());
            } else {
                timelines.remove(timelineWithEarliestPost);
            }
        }
        return amalgamatedPosts;
    }

    private LinkedList<Post> timelineWithEarliestPostIn(Stream<LinkedList<Post>> timelinesStream) {
        return timelinesStream.reduce(new LinkedList<>(), this::timelineWithEarliestPostBetween);
    }

    private LinkedList<Post> timelineWithEarliestPostBetween(LinkedList<Post> timeline1, LinkedList<Post> timeline2) {
        if (timeline1.size() > 0 && timeline2.size() > 0) {
            Post timeline1FirstPost = timeline1.peek();
            Post timeline2FirstPost = timeline2.peek();

            if (timeline1FirstPost.wasPostedBefore(timeline2FirstPost)) {
                return timeline1;
            } else {
                return timeline2;
            }
        } else if (timeline1.size() > 0) {
            return timeline1;
        } else {
            return timeline2;
        }
    }
}
