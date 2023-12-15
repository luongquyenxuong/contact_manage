package com.example.manage_contacts.security;

import com.example.manage_contacts.constant.Role;
import com.example.manage_contacts.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Ở đây,kiểm tra vai trò của người dùng và thêm quyền tương ứng vào danh sách authorities
        if (user.getRoleId().equals(Role.ADMIN.getRoleId()) ) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (user.getRoleId().equals(Role.USER.getRoleId())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
