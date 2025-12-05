package com.eob.shop.controller;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ProductService;
import com.eob.shop.service.ShopService;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/products")
public class ProductController {

    private final ProductService productService;
    private final ShopService shopService;

    /**
     * 상품 목록 페이지
     * URL: GET /shop/products
     *
     * 역할:
     * - 로그인한 판매자의 상점을 찾고
     * - 해당 상점의 모든 상품을 조회하여
     * - product-list.html 에 전달한다.
     */
    @GetMapping("")
    public String list(Model model, @AuthenticationPrincipal CustomSecurityDetail principal) {

        // 1) 로그인 정보 가져오기
        MemberEntity loginMember = principal.getMember();

        // 2) 로그인한 회원의 상점 정보 조회
        ShopEntity shop = shopService.findByMemberNo(loginMember.getMemberNo());

        // 3) 해당 상점의 상품 목록 조회
        model.addAttribute("productList",
                productService.findByShopNo(shop.getShopNo()));

        // 4) 목록 화면으로 이동
        return "shop/products/product-list";
    }

    /**
     * 상품 등록 페이지
     * URL: GET /shop/products/add
     *
     * 빈 ProductEntity 를 모델에 넣어
     * form 에서 th:object 로 활용한다.
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "shop/products/product-add";
    }

    /**
     * 상품 등록 처리
     * URL: POST /shop/products/add
     *
     * 역할:
     * - 로그인 판매자의 상점 정보 찾기
     * - 이미지 파일 저장
     * - 상품 엔티티 생성 및 저장(ProductService.save)
     */
    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductEntity product,
                            @RequestParam("imgFile") MultipartFile imgFile,
                            @AuthenticationPrincipal CustomSecurityDetail principal) throws Exception {

        // 로그인 회원 정보
        MemberEntity loginMember = principal.getMember();
        ShopEntity shop = shopService.findByMemberNo(loginMember.getMemberNo());

        // 1) 필수값 세팅
        product.setShop(shop);
        product.setCreatedAt(LocalDateTime.now());

        // 2) 이미지 저장 처리
        if (!imgFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imgFile.getOriginalFilename();
            String savePath = "C:/upload/product/" + fileName;
            imgFile.transferTo(new File(savePath));
            product.setImgUrl(fileName);
        }

        // 3) 저장하기
        productService.save(product);

        return "redirect:/shop/products";
    }

    /**
     * 상품 수정 페이지
     * URL: GET /shop/products/edit/{id}
     *
     * 역할:
     * - 기존 상품 정보를 조회하여
     * - 화면에 출력해 수정하고 저장할 수 있게 한다.
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {

        // 수정할 상품 조회
        ProductEntity product = productService.findById(id);

        model.addAttribute("product", product);
        return "shop/products/product-edit";
    }

    /**
     * 상품 수정 처리
     * URL: POST /shop/products/edit/{id}
     *
     * 역할:
     * - 기존 상품 엔티티 가져오기(findById)
     * - 입력받은 값으로 필드 업데이트
     * - 이미지 변경 시 파일 저장
     */
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id,
                            @ModelAttribute ProductEntity form,
                            @RequestParam(value = "imgFile", required = false) MultipartFile imgFile)
            throws Exception {

        // 기존 상품 가져오기
        ProductEntity product = productService.findById(id);

        // 1) 기본 필드 수정
        product.setProductName(form.getProductName());
        product.setSummary(form.getSummary());
        product.setIngredient(form.getIngredient());
        product.setCatName(form.getCatName());
        product.setPrice(form.getPrice());
        product.setStatus(form.getStatus());
        product.setUpdatedAt(LocalDateTime.now());

        // 2) 이미지 수정
        if (imgFile != null && !imgFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imgFile.getOriginalFilename();
            String savePath = "C:/upload/product/" + fileName;
            imgFile.transferTo(new File(savePath));

            product.setImgUrl(fileName);
        }

        // 3) 저장
        productService.save(product);

        return "redirect:/shop/products";
    }

    /**
     * 상품 삭제
     * URL: GET /shop/products/delete/{id}
     *
     * 실제 DB 삭제가 아니라 상태(status)를 DELETED 로 변경
     * → 소프트 삭제 처리
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {

        productService.delete(id);

        return "redirect:/shop/products";
    }
}

// 상품 관리 페이지로 넘어가도록 만들었음
// 하지만 입점 신청 등록이 안된 ID여서 상품 관리 페이지로 넘어가지 못함. shopNo가 null이기 때문.
// 추후 입점 신청 등록 기능을 만들고 나서 테스트 필요.
// 내일 할 일
// 1. 입점 신청 등록 기능 수정하기
// 2. 상품 관리 페이지로 넘어가는지 테스트 하기
