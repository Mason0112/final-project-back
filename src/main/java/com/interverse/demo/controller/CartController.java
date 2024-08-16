package com.interverse.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interverse.demo.dto.CartDTO;
import com.interverse.demo.model.Cart;
import com.interverse.demo.model.CartId;
import com.interverse.demo.model.Product;
import com.interverse.demo.model.User;
import com.interverse.demo.service.CartService;
import com.interverse.demo.service.ProductService;
import com.interverse.demo.service.UserService;


@RestController
public class CartController {
	@Autowired
	private CartService  cartService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/cart/addpost")
	public String createCart(@RequestBody  CartDTO cartdto){
		
		Product productById = productService.findProductById(cartdto.getProductsId());
		User userById = userService.findUserById(cartdto.getUsersId());
		Cart cart = new Cart();
		CartId cartId = new CartId();
		cartId.setProductsId(cartdto.getProductsId());
		cartId.setUsersId(cartdto.getUsersId());
		cart.setCartId(cartId);
		cart.setVol(cartdto.getVol());
		cart.setProducts(productById);
		cart.setUsers(userById);
		cartService.addOrUpdateCart(cart);
		
		return "ok";
	}
	
	

}
