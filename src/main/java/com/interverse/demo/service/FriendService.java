package com.interverse.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interverse.demo.dto.FriendDto;
import com.interverse.demo.model.Friend;
import com.interverse.demo.model.FriendId;
import com.interverse.demo.model.FriendRepository;
import com.interverse.demo.model.User;
import com.interverse.demo.model.UserRepository;

@Service
public class FriendService {

	@Autowired
	private FriendRepository friendRepo;

	@Autowired
	private UserRepository userRepo;
	
	public FriendDto convert(Friend friend) {
		FriendDto friendDto = new FriendDto();
		friendDto.setUser1Id(friend.getUser1().getId());
		friendDto.setUser2Id(friend.getUser2().getId());
		friendDto.setStatus(friend.getStatus());
		
		return friendDto;
	}

	public void switchFriendStatus(Integer user1Id, Integer user2Id) {
		Friend possibility1 = friendRepo.findByUser1IdAndUser2Id(user1Id, user2Id);
		Friend possibility2 = friendRepo.findByUser1IdAndUser2Id(user2Id, user1Id);

		Optional<User> optional1 = userRepo.findById(user1Id);
		User user1 = optional1.get();

		Optional<User> optional2 = userRepo.findById(user2Id);
		User user2 = optional2.get();

		// 自已加對方好友
		if (possibility1 == null && possibility2 == null) {

			FriendId friendId = new FriendId();
			friendId.setUser1Id(user1Id);
			friendId.setUser2Id(user2Id);

			Friend friend = new Friend();
			friend.setFriendId(friendId);
			friend.setUser1(user1);
			friend.setUser2(user2);
			friend.setStatus(false);

			friendRepo.save(friend);

			// 自己取消對對方的加好友申請
		} else if (possibility1 != null && possibility2 == null) {

			FriendId friendId = new FriendId();
			friendId.setUser1Id(user1Id);
			friendId.setUser2Id(user2Id);

			friendRepo.deleteById(friendId);
			// 自己接受對方加好友的邀請
		} else if (possibility1 == null && possibility2 != null) {

			possibility2.setStatus(true);
			friendRepo.save(possibility2);

			FriendId friendId = new FriendId();
			friendId.setUser1Id(user1Id);
			friendId.setUser2Id(user2Id);

			Friend friend = new Friend();
			friend.setFriendId(friendId);
			friend.setUser1(user1);
			friend.setUser2(user2);
			friend.setStatus(true);

			friendRepo.save(friend);
			// 刪除與對方的好友關係
		} else if (possibility1 != null && possibility2 != null) {

			FriendId friendId1 = new FriendId();
			friendId1.setUser1Id(user1Id);
			friendId1.setUser2Id(user2Id);

			friendRepo.deleteById(friendId1);

			FriendId friendId2 = new FriendId();
			friendId2.setUser1Id(user2Id);
			friendId2.setUser2Id(user1Id);

			friendRepo.deleteById(friendId2);

		}
	}

	public void declineRequest(Integer user1Id, Integer user2Id) {

		FriendId friendId = new FriendId();
		friendId.setUser1Id(user2Id);
		friendId.setUser2Id(user1Id);

		friendRepo.deleteById(friendId);
	}

	public List<FriendDto> findMyFriend(Integer user1Id) {

		List<Friend> friendList = friendRepo.findByUser1Id(user1Id);
		
		List<FriendDto> friendDtoList = friendList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
		
		return friendDtoList;
	}

	public List<FriendDto> findMyFriendRequest(Integer user2Id) {

		List<Friend> friendList = friendRepo.findByUser2Id(user2Id);
		
		List<FriendDto> friendDtoList = friendList.stream()
		        .map(this::convert)
		        .collect(Collectors.toList());
		
		return friendDtoList;
	}
	
}
