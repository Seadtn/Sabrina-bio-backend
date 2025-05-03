package com.sabrinaBio.application.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
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
	List<Product> findTop6ByActiveTrueOrderByQuantityAsc();

	List<Product> findTop4ByActiveTrueAndCategoryIdOrderByIdAsc(Long categoryId);

	List<Product> findByActiveTrueOrderByProductNewDesc();

	// Banner Section apis
	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio ,p.newPrice
			)
			FROM Product p
			WHERE p.promotion = true AND p.active = true
			ORDER BY p.startDate DESC
			""")
	List<BannerDTO> findTopPromotionalBanners(Pageable pageable);

	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio, p.newPrice
			)
			FROM Product p
			WHERE p.promotion = false AND p.productNew = true AND p.active = true
			ORDER BY p.creationDate DESC
			""")
	List<BannerDTO> findTop4NewBanners(Pageable pageable);

	@Query("""
			SELECT new com.sabrinaBio.application.Modal.DTO.BannerDTO(
			    p.id, p.name, p.nameFr, p.nameEng, p.image, p.promotion, p.soldRatio ,p.newPrice
			)
			FROM Product p
			WHERE p.promotion = false AND p.productNew = false AND p.active = true
			ORDER BY p.creationDate DESC
			""")
	List<BannerDTO> findRegularBanners(Pageable pageable);

	// Gett all products apis
	List<Product> findByActiveFalse();

	List<Product> findByActiveTrue();

	// Gett all new products
	List<Product> findByActiveTrueAndProductNewTrue();
	
	@Query(value = "SELECT * FROM product p WHERE p.active = true AND p.category_id IN :categoryIds ORDER BY p.id DESC LIMIT 9", nativeQuery = true)
	List<Product> findTop9ProductsByCategories(@Param("categoryIds") List<Long> categoryIds);

	@Query("SELECT p FROM Product p WHERE p.active = true AND (" + "p.startDate = :currentDate OR "
			+ "p.lastDate = :yesterdayDate)")
	List<Product> findByStartOrEndDate(@Param("currentDate") String currentDate,
			@Param("yesterdayDate") String yesterdayDate);

	List<Product> findTop6ByActiveTrueAndSouscategoryEnglishNameLike(String englishName);

	List<Product> findTop6ByActiveTrueAndPromotionTrueOrderByIdDesc();

	List<Product> findTop6ByActiveTrueOrderByIdDesc();

	@Query("""
			    SELECT p FROM Product p
			    WHERE p.active = true
			    AND (:categoryId IS NULL OR p.category.id = :categoryId)
			    AND (:subcategoryId IS NULL OR p.souscategory.id = :subcategoryId)
			    AND (
			        :search IS NULL OR
			        LOWER(REPLACE(p.name, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%')) OR
			        LOWER(REPLACE(p.nameFr, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%')) OR
			        LOWER(REPLACE(p.nameEng, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%'))
			    )
			    ORDER BY
			    CASE
			        WHEN :sort = 'highPrice' THEN p.price
			        WHEN :sort = 'lowPrice' THEN p.price
			        WHEN :sort = 'name' THEN p.name
			        ELSE p.id
			    END
			    DESC
			""")
	List<Product> findFilteredProducts(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId,
			@Param("search") String search, @Param("sort") String sort, Pageable pageable);

	@Query("""
			    SELECT p FROM Product p
			    WHERE p.active = true
			    AND (:categoryId IS NULL OR p.category.id = :categoryId)
			    AND (:subcategoryId IS NULL OR p.souscategory.id = :subcategoryId)
			    AND (
			        :search IS NULL OR
			        LOWER(REPLACE(p.name, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%')) OR
			        LOWER(REPLACE(p.nameFr, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%')) OR
			        LOWER(REPLACE(p.nameEng, 'ـ', '')) LIKE LOWER(CONCAT('%', REPLACE(:search, 'ـ', ''), '%'))
			    )
			""")
	Page<Product> findFilteredProductsTable(@Param("categoryId") Long categoryId,
			@Param("subcategoryId") Long subcategoryId, @Param("search") String search, Pageable pageable);

	@Query("SELECT new com.sabrinaBio.application.Modal.DTO.SearchDTO(" +
		       "p.id, p.name, p.nameFr, p.nameEng, p.image) " +
		       "FROM Product p WHERE " +
		       "REPLACE(REPLACE(REPLACE(REPLACE(p.name, 'ـ', ''), '  ', ' '), '  ', ' '), '  ', ' ') LIKE CONCAT('%', :name, '%') OR " +
		       "REPLACE(REPLACE(REPLACE(REPLACE(p.nameFr, 'ـ', ''), '  ', ' '), '  ', ' '), '  ', ' ') LIKE CONCAT('%', :name, '%') OR " +
		       "REPLACE(REPLACE(REPLACE(REPLACE(p.nameEng, 'ـ', ''), '  ', ' '), '  ', ' '), '  ', ' ') LIKE CONCAT('%', :name, '%')")
		List<SearchDTO> searchByName(@Param("name") String name);
	
	List<Product> findBySouscategoryId(Long id);

}
