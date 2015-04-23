package org.sydenham.blabber.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {

    public final User user;
    public final String message;
    public final LocalDateTime timestamp;

    public Post(User user, String message, LocalDateTime timestamp) {
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static Post from(User user, String msg) {
        return new Post(user, msg, LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(user, post.user) &&
                Objects.equals(message, post.message) &&
                Objects.equals(timestamp, post.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, message, timestamp);
    }
}
