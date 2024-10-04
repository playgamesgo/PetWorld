package me.playgamesgo.petworldbackend.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import me.playgamesgo.petworldbackend.models.Role;
import me.playgamesgo.petworldbackend.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter private final Long id;
    @Getter private final String username;
    @Getter private final String email;
    @Getter private final String phoneNumber;
    @JsonIgnore @Getter private final String password;
    @Getter private final Role role;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String phoneNumber, String password, Role role,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName().name()));

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.getRole(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
