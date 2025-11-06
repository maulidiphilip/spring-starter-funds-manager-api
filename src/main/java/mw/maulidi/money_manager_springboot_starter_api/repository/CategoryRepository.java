package mw.maulidi.money_manager_springboot_starter_api.repository;

import mw.maulidi.money_manager_springboot_starter_api.entity.CategoryEntity;
import mw.maulidi.money_manager_springboot_starter_api.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // select * from tbl_categories where profile.id = ?
    List<CategoryEntity> findByProfile_Id(Long profileId);

    // select * from tbl_categories where id = ?1 and profile.id = ?2
    Optional<CategoryEntity> findByIdAndProfile_Id(Long id, Long profileId);

    // select * from tbl_categories where type = ?1 and profile.id = ?2
    List<CategoryEntity> findByTypeAndProfile_Id(CategoryType type, Long profileId);

    Boolean existsByNameAndProfile_Id(String name, Long profileId);

}
