package com.interverse.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.interverse.demo.model.User;
import com.interverse.demo.model.UserDetail;
import com.interverse.demo.model.UserDetailRepository;
import com.interverse.demo.model.UserRepository;

@Service
public class UserService {

	@Value("${user.profile.photo.dir}")
	private String uploadDir;

	@Autowired
	private PasswordEncoder pwdEcoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserDetailRepository uDetailRepo;

	public User register(User user) {

		String encodedPassword = pwdEcoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		return userRepo.save(user);
	}

	public User login(String accountNumber, String password) {
		User user = userRepo.findByAccountNumber(accountNumber);

		if (user != null) {
			String encodedPassword = user.getPassword();
			boolean isMatched = pwdEcoder.matches(password, encodedPassword);

			if (isMatched) {
				return user;
			}
		}
		return null;
	}

	public List<User> findAllUser() {
		return userRepo.findAll();
	}

	public User findUserById(Integer id) {
		Optional<User> optional = userRepo.findById(id);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public void deleteUserById(Integer id) {
		userRepo.deleteById(id);
	}

	public User updateUserDetail(User user) {
		return userRepo.save(user);
	}

	public boolean existsByAccountNumber(String accountNumber) {
		User user = userRepo.findByAccountNumber(accountNumber);
		return user != null; // 如果user存在，返回true，否則返回false
	}

	public boolean existsByEmail(String email) {
		User user = userRepo.findByEmail(email);
		return user != null; // 如果user存在，返回true，否則返回false
	}

	public boolean existsByPhoneNumber(String phoneNumber) {
		UserDetail user = uDetailRepo.findByPhoneNumber(phoneNumber);
		return user != null; // 如果user存在，返回true，否則返回false
	}

	public User updatePhoto(Integer id, MultipartFile file) throws IOException {
		Optional<User> optional = userRepo.findById(id);

		if (optional.isEmpty()) {
			return null;
		} 
		
		User user = optional.get();
		UserDetail userDetail = user.getUserDetail();
		
		// 將路徑轉變為Path類別的的物件
		Path uploaPath = Paths.get(uploadDir);

		// 判斷目錄是否存在 不存在的話建立目錄
		if (!Files.exists(uploaPath)) {
			Files.createDirectories(uploaPath);
		}
		// 生成完整的路徑 eg C:/interverse/user_profile_photo/{id}.jpg
		Path filePath = uploaPath.resolve(id.toString());
		// 將Path轉型為File符合參數 將檔案寫入指定地點
		file.transferTo(filePath.toFile());
		
		userDetail.setPhoto(filePath.toString());
		
		return userRepo.save(user);
	}

}
