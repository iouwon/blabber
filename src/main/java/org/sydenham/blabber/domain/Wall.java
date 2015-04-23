package org.sydenham.blabber.domain;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Wall {

    private final LinkedList<Post> following;

    public Wall() {
        following = new LinkedList<>();
    }

    public Wall(LinkedList<Post> posts) {
        this.following = (LinkedList<Post>) posts.clone();
    }

    public Wall follow(User user) {
        return new Wall();
    }

    public void forEach(Consumer<Post> action) {
        following.forEach(action);
    }
}
