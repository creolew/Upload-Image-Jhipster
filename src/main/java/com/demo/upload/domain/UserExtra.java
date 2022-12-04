package com.demo.upload.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A UserExtra.
 */
@Entity
@Table(name = "user_extra")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "front_image")
    private String frontImage;

    @Column(name = "back_image")
    private String backImage;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserExtra id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrontImage() {
        return this.frontImage;
    }

    public UserExtra frontImage(String frontImage) {
        this.setFrontImage(frontImage);
        return this;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return this.backImage;
    }

    public UserExtra backImage(String backImage) {
        this.setBackImage(backImage);
        return this;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserExtra user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserExtra)) {
            return false;
        }
        return id != null && id.equals(((UserExtra) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtra{" +
            "id=" + getId() +
            ", frontImage='" + getFrontImage() + "'" +
            ", backImage='" + getBackImage() + "'" +
            "}";
    }
}
