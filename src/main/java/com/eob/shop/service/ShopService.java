package com.eob.shop.service;

import org.springframework.stereotype.Service;

import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public void saveShop(ShopEntity shop){
        shopRepository.save(shop);
    }
}
