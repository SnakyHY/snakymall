package com.snakyhy.snakymall.cart.service;

import com.snakyhy.snakymall.cart.vo.Cart;
import com.snakyhy.snakymall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {
    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中某个购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取整个购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车
     * @param tempCartKey
     */
    void clearCart(String tempCartKey);

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
