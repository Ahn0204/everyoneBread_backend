package com.eob.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.admin.model.repository.CategoryRepository;
import com.eob.admin.model.repository.DistanceFeeRepository;
import com.eob.main.model.service.MainService;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ProductService;
import com.eob.shop.service.ShopService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class mainController {
    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:/도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    public final CategoryRepository categoryRepository;
    public final MainService mainService;
    public final ProductService productService;
    public final ShopService shopService;
    public final DistanceFeeRepository distanceFeeRepository;

    // 메인 페이지
    @GetMapping("")
    public String getMainP() {
        return "main/main";
    }

    // 헤더 카테고리 항목 불러오기
    @GetMapping("getCategory")
    @ResponseBody
    public ResponseEntity<?> getCategory() {
        // 리턴 객체 선언
        List<String> category = new ArrayList<>();

        // 카테고리 DB에서 대분류명 List 가져오기
        Optional<ArrayList<String>> _category = categoryRepository.findByDepth(0);

        // DB에 값이 존재하고 && Optional객체가 비어있지 않다면
        if (_category.isPresent() && !_category.get().isEmpty()) {
            // category에 Optional의 값 모두 add
            category.addAll(_category.get());
            System.out.println("카테고리 불러오기 실행:" + category);
            // 카테고리 List보내기
            return ResponseEntity.ok(category);
        } else {
            // Optional값이 없다면
            return ResponseEntity.status(500).body("카테고리가 존재하지 않습니다.");
        }

    }

    /**
     * 상점 목록 페이지
     * 
     */
    @GetMapping("shopList")
    public String getShopList() {
        // shopList는 ajax로 불러오기
        return "main/shopList";
    }

    /**
     * 위치 기반 상점 검색 ajax응답
     */
    @PostMapping("getShopList")
    public String ajaxGetShopList(@RequestBody Map<String, Object> data,
            @RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        String category = (String) data.get("category");
        System.out.println(category);
        // 반경 내 상점목록 조회
        // pageable객체 생성-> distance 오름차순
        Pageable pageable = PageRequest.of(page, 8);
        // 상점내역 조회, 페이징 객체로 리턴
        Page<ShopEntity> shopList = mainService.getShopList(category, data, pageable);
        if (shopList != null && shopList.getTotalElements() > 0) {
            // 조회된 상점이 있다면
            model.addAttribute("shopList", shopList);
        }
        return "main/shopList-common";
    }

    /**
     * 상점 상세, 상품 목록 페이지
     * 
     * @param shopNo
     * @param model
     * @return productList.html
     */
    @GetMapping("shopList/productList/{shopNo}")
    public String getProductList(@PathVariable(name = "shopNo") long shopNo, @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng, Model model) {

        // shopNo에 해당하는 shop 조회
        ShopEntity shop = shopService.findByShopNo(shopNo);
        model.addAttribute("shop", shop);

        // 위치좌표와 shop 간의 거리 계산
        double distance = mainService.haversine(lat, lng, shop.getLatitude(), shop.getLongitude());
        distance = Math.floor(distance * 10) / 10; // km로 환산
        String d = null;
        // 배달비 계산
        int deliveryFee = 0;
        if (distance < 1) {
            d = (int) (distance * 1000 / 10) * 10 + "m"; // m로 환산
            deliveryFee = mainService.getDeliveryFeeByDistance(1);
        } else {
            d = Math.floor(distance * 10) / 10 + "km"; // km로 환산
            if (distance < 2) {
                deliveryFee = mainService.getDeliveryFeeByDistance(2);
            } else if (distance < 3) {
                deliveryFee = mainService.getDeliveryFeeByDistance(3);
            }
        }
        shop.setDistance(d); // distance저장
        model.addAttribute("deliveryFee", deliveryFee); // deliveryFee전달

        // shopNo에 해당하는 productList 조회
        List<ProductEntity> productList = productService.getProductList(shopNo);
        model.addAttribute("productList", productList);
        return "main/productList";
    }

    // 고객센터
    @GetMapping("customerCenter")
    public String getCenterP() {
        return "redirect:/customerCenter/notice";
    }

    // 공지페이지
    @GetMapping("/customerCenter/notice")
    public String getNoticeP() {
        return "customerCenter/notice";
    }

}
