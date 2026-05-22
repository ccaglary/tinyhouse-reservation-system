package com.tinyhouse.security;
import com.tinyhouse.entity.User; import com.tinyhouse.repository.UserRepository; import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service; import java.util.List;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isActive(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
