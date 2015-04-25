package org.sydenham.blabber.domain;

import org.sydenham.blabber.exception.ApplicationException;

import java.util.LinkedList;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class Timeline implements Cloneable {

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

    public LinkedList<Post> posts() {
        return (LinkedList<Post>) posts.clone();
    }

    public Integer length() {
        return posts.size();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeline timeline = (Timeline) o;
        return Objects.equals(posts, timeline.posts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(posts);
    }
}