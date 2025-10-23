package mw.maulidi.money_manager_springboot_starter_api.service;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.dto.ProfileDTO;
import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import mw.maulidi.money_manager_springboot_starter_api.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);

        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setIsActive(false); // ensure default inactive
        profileRepository.save(newProfile);
        // prepare an activation link
        String activationLink = "http://localhost:8080/api/v1/profiles/activate?activationToken=" + newProfile.getActivationToken();
        // send the link to the email
        String subject = "Activate your Account";
        String body = "<h2>Welcome to Money Manager, " + newProfile.getFullName() + "!</h2>" +
                "<p>Click the button below to activate your account:</p>" +
                "<a href=\"" + activationLink + "\" " +
                "style=\"display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;" +
                "text-decoration:none;border-radius:5px;\">Activate Account</a>" +
                "<p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "<p><a href=\"" + activationLink + "\">" + activationLink + "</a></p>";

        // make use of the email service
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
    }

    // Convert DTO -> Entity
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .Id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    // Convert Entity -> DTO
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

    // validate the profile
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return  true;
                }).orElse(false);
    }
}
