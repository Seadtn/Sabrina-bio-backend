package com.sabrinaBio.application.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.DTO.BannerDTO;
import com.sabrinaBio.application.Modal.DTO.SearchDTO;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Most seller function
	List<Product> findTop5ByActiveTrueOrderByQuantityAsc();

	List<Product> findTop4ByActiveTrueAndCategoryIdOrderByIdAsc(Long categoryId);

	List<Product> findByActiveTrueOrderByProductNewDesc();
	// Banner Section apis 
	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio
			)
			FROM Product p
			WHERE p.promotion = true AND p.active = true
			ORDER BY p.startDate DESC
			""")
	List<BannerDTO> findTop2PromotionalBanners();

	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio
			)
			FROM Product p
			WHERE p.promotion = false AND p.productNew = true AND p.active = true
			ORDER BY p.creationDate DESC
			""")
	List<BannerDTO> findTop2NewBanners();

	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio
			)
			FROM Product p
			WHERE p.promotion = false AND p.productNew = false AND p.active = true
			ORDER BY p.creationDate DESC
			""")
	List<BannerDTO> findRegularBanners();
	
	// Gett all products apis 
	List<Product> findByActiveFalse();

	List<Product> findByActiveTrue();
	// Gett all new products 
	List<Product> findByActiveTrueAndProductNewTrue();

	@Query("SELECT p FROM Product p WHERE p.active = true AND (" + "p.startDate = :currentDate OR "
			+ "p.lastDate = :yesterdayDate)")
	List<Product> findByStartOrEndDate(@Param("currentDate") String currentDate,
			@Param("yesterdayDate") String yesterdayDate);
	
	List<Product> findTop6ByActiveTrueAndSouscategoryEnglishNameLike(String englishName);
	
    @Query("""
            SELECT p FROM Product p 
            WHERE p.active = true 
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:subcategoryId IS NULL OR p.souscategory.id = :subcategoryId)
            AND (:search IS NULL OR 
                LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(p.nameFr) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(p.nameEng) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY 
            CASE 
                WHEN :sort = 'highPrice' THEN p.price
                WHEN :sort = 'lowPrice' THEN p.price
                WHEN :sort = 'name' THEN p.name
                ELSE p.id
            END
            DESC
        """)
        List<Product> findFilteredProducts(
            @Param("categoryId") Long categoryId,
            @Param("subcategoryId") Long subcategoryId,
            @Param("search") String search,
            @Param("sort") String sort,
            Pageable pageable
        );
	
	@Query("SELECT new com.sabrinaBio.application.Modal.DTO.SearchDTO(" +
		       "p.id,p.name, p.nameFr, p.nameEng, p.image" +
		       ") FROM Product p WHERE " +
		       "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
		       "LOWER(p.nameFr) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
		       "LOWER(p.nameEng) LIKE LOWER(CONCAT('%', :name, '%'))")
		List<SearchDTO> searchByName(String name);
}
