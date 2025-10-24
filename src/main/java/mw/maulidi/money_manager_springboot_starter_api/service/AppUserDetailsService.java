package mw.maulidi.money_manager_springboot_starter_api.service;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import mw.maulidi.money_manager_springboot_starter_api.repository.ProfileRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service responsible for integrating custom ProfileEntity users
 * with Spring Security’s authentication mechanism.
 *
 * This class tells Spring Security how to fetch user data (email, password)
 * from the database during the login process.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    /**
     * Loads a user's details from the database using their email.
     *
     * @param email the user's email address (used as username)
     * @return a Spring Security {@link UserDetails} object
     * @throws UsernameNotFoundException if no profile is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));

        // Return a Spring Security compatible User object
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList()) // No roles or authorities yet
                .build();
    }
}
