package com.interverse.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubPhotoRepository extends JpaRepository<ClubPhoto, Integer> {
	
	//尋找club中所有照片
	List<ClubPhoto> findByClubId(Integer clubId);
}
