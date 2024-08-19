package com.interverse.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.interverse.demo.model.PostComment;
import com.interverse.demo.service.PostCommentService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class PostCommentController {

	@Autowired
	private PostCommentService commentService;
	
	@PostMapping(
//			consumes =MediaType.MULTIPART_FORM_DATA_VALUE , name=
			"/postComment/addComment")
	public PostComment addComment(@RequestBody PostComment postComment) {
		return commentService.addComment(postComment);
	}
	
	@PutMapping("/postComment/{commentId}")
	public PostComment updateComment(@PathVariable Integer commentId, @RequestParam String newComment) {
		return commentService.updateComment(commentId, newComment);
	}
	
	@DeleteMapping("/postComment/{commentId}")
	public void deleteComment(@PathVariable Integer commentId) {
		commentService.deleteCommentById(commentId);
	}
	
	@GetMapping("/postComment/{postId}")
	public List<PostComment> showPostComment(@PathVariable Integer postId) {
		return commentService.FindCommentByPost(postId);
	}
	
}