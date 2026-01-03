package com.eob.shop.controller;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ProductStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.model.dto.ProductDetailResponse;
import com.eob.shop.service.ProductService;
import com.eob.shop.service.ShopService;

import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/**")
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
     * - 1~10 페이지 묶음 계산
     */
    @GetMapping("products")
    public String list(@RequestParam(defaultValue = "0", name="page") int page, Model model, @AuthenticationPrincipal CustomSecurityDetail principal) {

        // 1) 로그인 정보 가져오기
        MemberEntity loginMember = principal.getMember();

        // 2) 로그인한 회원의 상점 정보 조회
        ShopEntity shop = shopService.findByMemberNo(loginMember.getMemberNo());

        if(shop == null){
            // 상점 정보가 없으면 에러 페이지 or 상점 등록 페이지로 리다이렉트
            return "redirect:/shop/register/step"; // 예: 상점 등록 페이지로 리다이렉트
        }

        // 3) 페이징 처리된 상품 목록 조회 (한 페이지 당 10개)
        Page<ProductEntity> productPage = productService.findByShopNo(shop.getShopNo(), page, 10);

        // 4) 1~10 페이지 묶음 계산
        int currentPage = productPage.getNumber();
        int totalPages = productPage.getTotalPages();   // 전체 페이지 수 
        int startPage = (currentPage / 10) * 10;       // 시작 페이지 번호
        int endPage = Math.min(startPage + 9, totalPages - 1); // 끝 페이지 번호, 총 페이지 수를 넘지 않도록

        // 5) 화면에 전달할 데이터
        model.addAttribute("productList", productPage.getContent()); // 현재 페이지의 상품 목록
        model.addAttribute("productPage", productPage);              // 페이지 정보
        model.addAttribute("currentPage", currentPage); // 현재 페이지 번호
        model.addAttribute("startPage", startPage);     // 시작 페이지 번호
        model.addAttribute("endPage", endPage);         // 끝 페이지 번호

        // 6) 상품 관리 화면으로 이동
        return "shop/shop-products";
    }

    /**
     * 상품 등록 페이지
     * URL: GET /shop/products/add
     *
     * 빈 ProductEntity 를 모델에 넣어
     * form 에서 th:object 로 활용한다.
     */
    @GetMapping("products/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "shop/shop-product-add";
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
    @PostMapping("products/add")
    public String addProduct(@ModelAttribute ProductEntity product,
                            @RequestParam("image") MultipartFile imageFile,
                            @AuthenticationPrincipal CustomSecurityDetail principal) throws Exception {

        // 로그인 회원 정보
        MemberEntity loginMember = principal.getMember();
        // 로그인 회원의 상점 정보 조회
        ShopEntity shop = shopService.findByMemberNo(loginMember.getMemberNo());

        // 1) 필수값 세팅
        // 상점 연결
        product.setShop(shop);
        product.setCreatedAt(LocalDateTime.now());

        // 2) 이미지 저장 처리
        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            String savePath = "C:/upload/product/" + fileName;

            // 디렉토리 없으면 생성
            File directory = new File("C:/upload/product/");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            imageFile.transferTo(new File(savePath));
            // 엔티티에 파일명 저장
            product.setImgUrl(fileName);
        }

        // 3) 저장하기
        productService.save(product);

        return "redirect:/shop/products";
    }

    /**
     * 상품 상세 조회 (모달)
     * URL : GET /shop/products/{id}/detail
     */
    @GetMapping("products/{id}/detail")
    @ResponseBody
    public ProductDetailResponse productDetail(@PathVariable("id") Long id, @AuthenticationPrincipal CustomSecurityDetail principal ){
        ProductEntity product = productService.findById(id);

        // 내 상점 상품만 조회 가능
        Long memberNo = principal.getMember().getMemberNo();
        if(!product.getShop().getMember().getMemberNo().equals(memberNo)){
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        return new ProductDetailResponse(product);
    }

    /**
     * 상품 수정 (상품 상세 조회 모달에서 AJAX)
     * URL : POST /shop/products/{id}
     */
    @PostMapping("products/{id}")
    @ResponseBody
    public void updateProduct(@PathVariable("id") Long id, @RequestBody ProductEntity form, @AuthenticationPrincipal CustomSecurityDetail principal){
        // 기존 상품 조회
        ProductEntity product = productService.findById(id);

        // 판매자 본인의 상품인지 확인
        Long memberNo = principal.getMember().getMemberNo();
        if(!product.getShop().getMember().getMemberNo().equals(memberNo)){
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        // ===== 필드 업데이트 =====
        product.setProductName(form.getProductName());
        product.setPrice(form.getPrice());
        product.setSummary(form.getSummary());
        product.setIngredient(form.getIngredient());
        product.setCatName(form.getCatName());
        product.setStatus(form.getStatus());
        product.setUpdatedAt(LocalDateTime.now());

        // 저장
        productService.save(product);
    }

    /**
     * 상품 목록 드롭다운 / 모달 "판매중, 품절" 버튼 전용
     */
    @PostMapping("products/{id}/status")
    @ResponseBody
    public void updateProductStatus(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal CustomSecurityDetail principal){
        ProductEntity product = productService.findById(id);

        Long memberNo = principal.getMember().getMemberNo();
        if(!product.getShop().getMember().getMemberNo().equals(memberNo)){
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        ProductStatus status = ProductStatus.valueOf(body.get("status"));
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());

        productService.save(product);
    }

    /**
     * 상품 삭제
     * URL: GET /shop/products/delete/{id}
     *
     * 실제 DB 삭제가 아니라 상태(status)를 DELETED 로 변경
     * → 소프트 삭제 처리
     */
    // @GetMapping("products/delete/{id}")
    // public String delete(@PathVariable("id") Long id, @AuthenticationPrincipal CustomSecurityDetail principal) {

    //     // 본인의 상품인지 확인
    //     ProductEntity product = productService.findById(id);

    //     // 권한 체크
    //     Long memberNo = principal.getMember().getMemberNo();
    //     if(!product.getShop().getMember().getMemberNo().equals(memberNo)){
    //         throw new RuntimeException("삭제 권한이 없습니다.");
    //     }

    //     // 상품 삭제 처리 (soft delete)
    //     productService.delete(id);

    //     return "redirect:/shop/products";
    // }

    /**
     * 상품 삭제 (상품 상세 모달)
     * URL : POST /shop/products/{id}/delete
     */
    @PostMapping("products/{id}/delete")
    @ResponseBody
    public void deleteProductAjax(@PathVariable("id") Long id, @AuthenticationPrincipal CustomSecurityDetail principal){

        // 상품 조회
        ProductEntity product = productService.findById(id);

        // 보안 체크 : 내 상점 상품만 삭제 가능
        Long memberNo = principal.getMember().getMemberNo();
        if(!product.getShop().getMember().getMemberNo().equals(memberNo)){
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 실제 삭제 X, 상태만 변경
        product.setStatus(ProductStatus.DELETED);
        product.setUpdatedAt(LocalDateTime.now());

        productService.save(product);
    }
}