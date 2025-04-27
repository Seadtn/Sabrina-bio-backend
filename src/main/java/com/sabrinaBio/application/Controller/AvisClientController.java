package com.sabrinaBio.application.Controller;

import com.sabrinaBio.application.Modal.AvisClient;
import com.sabrinaBio.application.Modal.AvisType;
import com.sabrinaBio.application.Repository.AvisClientRepository;
import com.sabrinaBio.application.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/avisClientManagement/")
@RequiredArgsConstructor
public class AvisClientController {

	private final AvisClientRepository avisClientRepository;
	private final ProductRepository productRepository;

	@PostMapping("/addNewTestimonial")
	public ResponseEntity<?> addNewTestimonial(@RequestBody Map<String, Object> request) {
		try {
			AvisClient avisClient = new AvisClient();

			String typeStr = (String) request.get("type");
			AvisType type = AvisType.fromString(typeStr);
			avisClient.setType(type);

			Boolean active = (Boolean) request.get("active");
			avisClient.setActive(active != null ? active : true);

			if (request.containsKey("productId") && request.get("productId") != null) {
				Long productId = Long.parseLong(request.get("productId").toString());
				productRepository.findById(productId).ifPresent(avisClient::setProduct);
			}

			if (type == AvisType.beforeAfter) {
				handleBeforeAfterImages(request, avisClient);
			} else {
				handleCommentImages(request, avisClient);
			}

			AvisClient savedAvis = avisClientRepository.save(avisClient);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedAvis);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating testimonial: " + e.getMessage());
		}
	}

	@PutMapping("updateTestimonial/{id}")
	public ResponseEntity<?> updateTestimonial(@PathVariable Long id, @RequestBody Map<String, Object> request) {
		try {
			Optional<AvisClient> existingAvisOpt = avisClientRepository.findById(id);

			if (!existingAvisOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Testimonial with id " + id + " not found");
			}

			AvisClient existingAvis = existingAvisOpt.get();

			if (request.containsKey("type")) {
				String typeStr = (String) request.get("type");
				existingAvis.setType(AvisType.fromString(typeStr));
			}

			if (request.containsKey("active")) {
				Boolean active = (Boolean) request.get("active");
				existingAvis.setActive(active != null ? active : true);
			}

			if (request.containsKey("productId")) {
				if (request.get("productId") != null) {
					Long productId = Long.parseLong(request.get("productId").toString());
					productRepository.findById(productId).ifPresent(existingAvis::setProduct);
				} else {
					existingAvis.setProduct(null);
				}
			}

			if (existingAvis.getType() == AvisType.beforeAfter) {
				updateBeforeAfterImages(request, existingAvis);
			} else {
				handleCommentImages(request, existingAvis);
			}

			AvisClient updatedAvis = avisClientRepository.save(existingAvis);
			return ResponseEntity.ok(updatedAvis);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating testimonial: " + e.getMessage());
		}
	}

	@DeleteMapping("deleteTestimonial/{id}")
	public ResponseEntity<?> deleteTestimonial(@PathVariable Long id) {
		try {
			Optional<AvisClient> existingAvis = avisClientRepository.findById(id);
			if (!existingAvis.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Testimonial with id " + id + " not found");
			}
			avisClientRepository.deleteById(id);
			return ResponseEntity.ok().body("Testimonial with id " + id + " deleted successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting testimonial: " + e.getMessage());
		}
	}

	@GetMapping("getActiveTestimonials")
	public ResponseEntity<?> getActiveTestimonials() {
		try {
			List<AvisClient> activeAvis = avisClientRepository.findAll().stream().filter(AvisClient::isActive)
					.collect(Collectors.toList());

			List<Map<String, Object>> testimonialDTOs = activeAvis.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			return ResponseEntity.ok(testimonialDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching active testimonials: " + e.getMessage());
		}
	}

	@GetMapping("getAllTestimonials")
	public ResponseEntity<?> getAllTestimonials() {
		try {
			List<AvisClient> allAvis = avisClientRepository.findAll();
			List<Map<String, Object>> testimonialDTOs = allAvis.stream().map(this::convertToDTO)
					.collect(Collectors.toList());
			return ResponseEntity.ok(testimonialDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching all testimonials: " + e.getMessage());
		}
	}

	@GetMapping("getAllTestimonialsbyPages")
	public ResponseEntity<?> getAllTestimonialsbyPages(@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		try {
			Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
			Pageable pageable = PageRequest.of(offset, limit, Sort.by(direction, sortBy));
			Page<AvisClient> avisPage = avisClientRepository.findAll(pageable);

			List<Map<String, Object>> testimonialDTOs = avisPage.getContent().stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			Map<String, Object> response = new HashMap<>();
			response.put("content", testimonialDTOs);
			response.put("currentPage", avisPage.getNumber());
			response.put("totalItems", avisPage.getTotalElements());
			response.put("totalPages", avisPage.getTotalPages());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching testimonials: " + e.getMessage());
		}
	}

	@GetMapping("getTestimonial/{id}")
	public ResponseEntity<?> getTestimonialById(@PathVariable Long id) {
		try {
			Optional<AvisClient> avis = avisClientRepository.findById(id);
			if (!avis.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Testimonial with id " + id + " not found");
			}
			return ResponseEntity.ok(convertToDTO(avis.get()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching testimonial: " + e.getMessage());
		}
	}

	@PutMapping("toggleActiveStatus/{id}")
	public ResponseEntity<?> toggleActiveStatus(@PathVariable Long id) {
		try {
			Optional<AvisClient> avis = avisClientRepository.findById(id);
			if (!avis.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Testimonial with id " + id + " not found");
			}
			AvisClient existingAvis = avis.get();
			existingAvis.setActive(!existingAvis.isActive());
			AvisClient updatedAvis = avisClientRepository.save(existingAvis);
			return ResponseEntity.ok(convertToDTO(updatedAvis));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error toggling testimonial active status: " + e.getMessage());
		}
	}

	private void handleBeforeAfterImages(Map<String, Object> request, AvisClient avisClient) {
		avisClient.setBeforeImageUrl(decodeBase64Field(request.get("beforeImageUrl")));
		avisClient.setAfterImageUrl(decodeBase64Field(request.get("afterImageUrl")));
		avisClient.setAvisImageUrl(decodeBase64Field(request.get("avisImageUrl")));
	}

	private void updateBeforeAfterImages(Map<String, Object> request, AvisClient avisClient) {
		if (request.containsKey("beforeImageUrl")) {
			avisClient.setBeforeImageUrl(decodeBase64Field(request.get("beforeImageUrl")));
		}
		if (request.containsKey("afterImageUrl")) {
			avisClient.setAfterImageUrl(decodeBase64Field(request.get("afterImageUrl")));
		}
		if (request.containsKey("avisImageUrl")) {
			avisClient.setAvisImageUrl(decodeBase64Field(request.get("avisImageUrl")));
		}
	}

	private void handleCommentImages(Map<String, Object> request, AvisClient avisClient) {
		avisClient.setCommentImage1(
				request.get("commentImage1") != null ? decodeBase64Field(request.get("commentImage1")) : null);
		avisClient.setCommentImage2(
				request.get("commentImage2") != null ? decodeBase64Field(request.get("commentImage2")) : null);
		avisClient.setCommentImage3(
				request.get("commentImage3") != null ? decodeBase64Field(request.get("commentImage3")) : null);
		avisClient.setCommentImage4(
				request.get("commentImage4") != null ? decodeBase64Field(request.get("commentImage4")) : null);
	}

	private Map<String, Object> convertToDTO(AvisClient avis) {
		Map<String, Object> dto = new HashMap<>();

		dto.put("id", avis.getId());
		dto.put("type", avis.getType().toString());
		dto.put("active", avis.isActive());

		if (avis.getProduct() != null) {
			dto.put("productId", avis.getProduct().getId());
			dto.put("productName", avis.getProduct().getName());
		}

		if (avis.getType() == AvisType.beforeAfter) {
			dto.put("beforeImageUrl", encodeImage(avis.getBeforeImageUrl()));
			dto.put("afterImageUrl", encodeImage(avis.getAfterImageUrl()));
			dto.put("avisImageUrl", encodeImage(avis.getAvisImageUrl()));
		} else {
			List<String> images = new ArrayList<>();
			if (avis.getCommentImage1() != null)
				images.add(encodeImage(avis.getCommentImage1()));
			if (avis.getCommentImage2() != null)
				images.add(encodeImage(avis.getCommentImage2()));
			if (avis.getCommentImage3() != null)
				images.add(encodeImage(avis.getCommentImage3()));
			if (avis.getCommentImage4() != null)
				images.add(encodeImage(avis.getCommentImage4()));
			dto.put("images", images);
		}

		return dto;
	}

	private byte[] decodeBase64Image(String base64Image) {
		if (base64Image.contains(",")) {
			base64Image = base64Image.split(",")[1];
		}
		return Base64.getDecoder().decode(base64Image);
	}

	private byte[] decodeBase64Field(Object field) {
		if (field == null)
			return null;
		String base64Image = (String) field;
		return decodeBase64Image(base64Image);
	}

	private String encodeImage(byte[] imageBytes) {
		if (imageBytes == null)
			return null;
		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
