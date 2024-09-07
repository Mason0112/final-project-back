package com.interverse.demo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interverse.demo.dto.EventPhotoDTO;
import com.interverse.demo.model.EventPhoto;
import com.interverse.demo.service.EventPhotoService;

@RestController
@RequestMapping("/eventPhoto")
public class EventPhotoController {

	@Autowired
	private EventPhotoService epService;

	// 建立
	@PostMapping("/new")
	public ResponseEntity<?> createEventPhoto(@RequestParam("file") MultipartFile file,
			@RequestParam("eventId") Integer eventId, @RequestParam("uploaderId") Integer uploaderId) {
		try {
			EventPhoto savedPhoto = epService.createEventPhoto(file, eventId, uploaderId);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedPhoto);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 尋找event裡所有照片
	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<EventPhotoDTO>> getAllPhotoByEventId(@PathVariable Integer eventId) {
		try {
			List<EventPhoto> allByEventId = epService.findAllByEventId(eventId);
			List<EventPhotoDTO> photoDTO = allByEventId.stream().map(this::convertToDTO).collect(Collectors.toList());
			return ResponseEntity.ok(photoDTO);
		} catch (Exception e) {
			System.out.println("錯誤 " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}

	}

	// user可以在event中刪除自己上傳的照片
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteEventPhoto(@PathVariable Integer id, @RequestParam Integer uploaderId) {
		if (uploaderId == null) {
			return ResponseEntity.badRequest().body("Uploader ID is required.");
		}
		try {
			epService.deletePhotoIfOwner(id, uploaderId);
			return ResponseEntity.ok("Photo deleted successfully.");
		} catch (SecurityException | IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting the file.");
		}
	}

	// 轉換DTO
	public EventPhotoDTO convertToDTO(EventPhoto eventPhoto) {
		EventPhotoDTO dto = new EventPhotoDTO();

		dto.setId(eventPhoto.getId());
//		   dto.setPhoto(eventPhoto.getPhoto());
		dto.setEventId(eventPhoto.getEvent().getId());
		dto.setUploaderId(eventPhoto.getUploaderId().getId());
		dto.setUserName(eventPhoto.getUploaderId().getNickname());

		return dto;
	}

	@GetMapping("/{eventId}/{photoId}")
	public ResponseEntity<Resource> getSpecificEventPhoto(@PathVariable Integer eventId, @PathVariable Integer photoId)
			throws MalformedURLException {
		// 使用 eventId 和 photoId 來獲取特定的照片
		EventPhoto photo = epService.getEventPhoto(eventId, photoId);

		if (photo == null) {
			return ResponseEntity.notFound().build();
		}

		String photoPath = photo.getPhoto();

		Path path = Paths.get(photoPath);
		Resource resource = new UrlResource(path.toUri());

		if (resource.exists() || resource.isReadable()) {
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getPhoto() + "\"")
					.contentType(MediaType.IMAGE_JPEG) // 或者根據實際情況設置
					.body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
