package org.sydenham.blabber.domain;

import java.util.Objects;

public class User {

    public final String name;
    public final Timeline timeline;

    public User(String name) {
        this.name = name;
        this.timeline = new Timeline();
    }

    public User(String name, Timeline timeline) {
        this.name = name;
        this.timeline = (Timeline) timeline.clone();
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

    public User postMessage(String msg) {
        return new User(name, ((Timeline) timeline.clone()).append(Post.from(this, msg)));
    }
}
