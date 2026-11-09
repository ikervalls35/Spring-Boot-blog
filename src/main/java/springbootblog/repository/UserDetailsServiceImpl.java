package springbootblog.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;
import springbootblog.model.User;
import springbootblog.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String identifier = usernameOrEmail == null ? "" : usernameOrEmail.trim();
        User appUser = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier.toLowerCase()))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identifier));

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername());
        builder.password(appUser.getPassword());
        builder.roles(appUser.getRole()); // Asegúrate de que el rol coincida (ej. "ADMIN")

        return builder.build();
    }
}