package org.sydenham.blabber.domain;

import java.util.LinkedList;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Timeline {

    private final LinkedList<Post> posts;

    public Timeline() {
        posts = new LinkedList<>();
    }

    public Timeline(LinkedList<Post> posts) {
        this.posts = (LinkedList<Post>) posts.clone();
    }

    public Timeline append(Post newPost) {
        LinkedList<Post> newPosts = (LinkedList<Post>) posts.clone();
        newPosts.add(newPost);
        return new Timeline(newPosts);
    }

    public void forEach(Consumer<Post> action) {
        posts.forEach(action);
    }

    public Integer length() {
        return posts.size();
    }
}
