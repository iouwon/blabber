package org.sydenham.blabber.domain;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Wall {

    private final HashSet<User> following;

    public Wall() {
        following = new HashSet<>();
    }

    public Wall(HashSet<User> following) {
        this.following = (HashSet<User>) following.clone();
    }

    public Wall follow(User user) {
        HashSet<User> newInterests = (HashSet<User>) following.clone();
        newInterests.add(user);
        return new Wall(newInterests);
    }

    public Wall capturePostsOf(HashSet<User> users) {
        HashSet<User> usersLatestPosts = (HashSet<User>) users.clone();
        usersLatestPosts.retainAll(following);
        return new Wall(usersLatestPosts);
    }

    public void forEach(Consumer<Post> action) {
        amalgamatedPosts().forEach(action);
    }

    private List<Post> amalgamatedPosts() {
        LinkedList<Post> amalgamatedPosts = new LinkedList<>();
        LinkedList<LinkedList<Post>> timelines = new LinkedList<>();
        following.forEach((user) -> timelines.add(user.timeline.posts()));
        while (timelines.size() > 0) {
            Stream<LinkedList<Post>> timelinesStream = timelines.stream();
            LinkedList<Post> timelineWithEarliestPost = timelineWithEarliestPostIn(timelinesStream);
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
            if (timeline1.peek().wasPostedBefore(timeline2.peek())) {
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
