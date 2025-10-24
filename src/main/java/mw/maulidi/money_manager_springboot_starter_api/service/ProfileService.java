package mw.maulidi.money_manager_springboot_starter_api.service;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.dto.ProfileDTO;
import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import mw.maulidi.money_manager_springboot_starter_api.repository.ProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ProfileService
 * ----------------------
 * This service handles profile-related logic such as:
 * - Registration and account activation
 * - Password encoding
 * - Retrieving the currently logged-in user's profile
 * - Fetching public profile information
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new profile, encodes the password, generates an activation token,
     * saves the user, and sends an email with an activation link.
     *
     * @param profileDTO the registration data
     * @return a DTO of the newly registered profile
     */
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);

        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setIsActive(false);
        profileRepository.save(newProfile);

        // Create the activation link
        String activationLink = "http://localhost:8080/api/v1/profiles/activate?activationToken=" + newProfile.getActivationToken();

        // Email message body (HTML)
        String subject = "Activate your Account";
        String body = "<h2>Welcome to Money Manager, " + newProfile.getFullName() + "!</h2>" +
                "<p>Click the button below to activate your account:</p>" +
                "<a href=\"" + activationLink + "\" " +
                "style=\"display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;" +
                "text-decoration:none;border-radius:5px;\">Activate Account</a>" +
                "<p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "<p><a href=\"" + activationLink + "\">" + activationLink + "</a></p>";

        // Send activation email
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    /**
     * Converts a ProfileDTO into a ProfileEntity.
     * Encodes the password before saving to the database.
     */
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .Id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    /**
     * Converts a ProfileEntity into a ProfileDTO for API responses.
     */
    public ProfileDTO toDTO(ProfileEntity entity) {
        return ProfileDTO.builder()
                .Id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Activates a profile using the provided activation token.
     *
     * @param activationToken unique token from the email link
     * @return true if the profile is successfully activated, false otherwise
     */
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    /**
     * Checks if a user's account is active based on their email.
     */
    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    /**
     * Retrieves the currently authenticated user's profile entity
     * from the Spring Security context.
     */
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile Not Found with email: " + authentication.getName()));
    }

    /**
     * Retrieves a public profile either for the current user or another user by email.
     *
     * @param email optional email parameter (if null, returns the current user's profile)
     * @return ProfileDTO representing the profile information
     */
    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser;

        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile Not Found with email: " + email));
        }

        return ProfileDTO.builder()
                .Id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .password(currentUser.getPassword())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }
}
