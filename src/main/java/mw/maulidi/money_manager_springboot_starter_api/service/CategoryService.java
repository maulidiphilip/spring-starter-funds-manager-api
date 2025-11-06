package mw.maulidi.money_manager_springboot_starter_api.service;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.dto.CategoryDTO;
import mw.maulidi.money_manager_springboot_starter_api.entity.CategoryEntity;
import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import mw.maulidi.money_manager_springboot_starter_api.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    /**
     * Creates a new category for the authenticated user.
     */
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();

        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
        }

        if (categoryDTO.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category type is required");
        }

        if (categoryRepository.existsByNameAndProfile_Id(categoryDTO.getName(), profile.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);

        return toDTO(newCategory);
    }

    /** Converts a CategoryDTO into a CategoryEntity for saving in the database. */
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profile(profile)
                .build();
    }
    /** Converts a CategoryEntity into a CategoryDTO for returning to clients. */
    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .description(entity.getDescription()) // <-- ADD THIS LINE
                .icon(entity.getIcon())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
