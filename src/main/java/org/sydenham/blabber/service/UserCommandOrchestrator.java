package org.sydenham.blabber.service;

import org.sydenham.blabber.domain.Post;
import org.sydenham.blabber.domain.User;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class UserCommandOrchestrator {
    private final HashMap<User, User> users;

    private UserCommandOrchestrator() {
        this.users = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private UserCommandOrchestrator(HashMap<User, User> users) {
        this.users = (HashMap<User, User>) users.clone();
    }

    public static UserCommandOrchestrator newObj() {
        return new UserCommandOrchestrator();
    }

    @SuppressWarnings("unchecked")
    public UserCommandOrchestrator postMessage(String username, String msg) {
        User poster = User.from(username);
        HashMap<User, User> newUsers = (HashMap<User, User>) users.clone();

        User posterWithNewPost = newUsers.getOrDefault(poster, poster).postMessage(msg);
        newUsers.put(posterWithNewPost, posterWithNewPost);
        return new UserCommandOrchestrator(newUsers);
    }

    public void forEachTimelinePostOf(String username, Consumer<Post> action) {
        User user = User.from(username);
        processUsersMessages(user, theUser -> theUser.timeline.posts(), action);
    }

    @SuppressWarnings("unchecked")
    public UserCommandOrchestrator userAFollowsUserB(String followerName, String followedName) {
        User follower = User.from(followerName);
        User followed = User.from(followedName);
        HashMap<User, User> newUsers = (HashMap<User, User>) users.clone();
        follower = newUsers.getOrDefault(follower, follower);
        followed = newUsers.getOrDefault(followed, followed);

        follower = follower.follow(followed);

        newUsers.put(follower, follower);
        newUsers.put(followed, followed);

        return new UserCommandOrchestrator(newUsers);
    }

    public void forEachWallPostOf(String username, Consumer<Post> action) {
        User user = User.from(username);
        user = users.getOrDefault(user, user);
        user = user.harvest(new HashSet<>(users.values()));
        user.wall.forEach(action);
    }

    private void processUsersMessages(User user, Function<User, LinkedList<Post>> postsOfUser, Consumer<Post> action) {
        users.computeIfPresent(user, (userKey, theUser) -> {
            LinkedList<Post> userPosts = postsOfUser.apply(theUser);
            userPosts.forEach(action);
            return theUser;
        });
    }
}
