package com.eob.shop.controller;

import java.io.File;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.service.MemberService;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ProductService;
import com.eob.shop.service.ShopService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/*")
public class ShopController {

    private final MemberService memberService;
    private final ShopService shopService;
    private final ProductService productService;
    // location저장용 - Point객체 생성 객체
    private final static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 판매자 로그인 페이지
     * URL: GET /shop/login
     *
     * - SecurityConfig 에서 permitAll 되어 있음
     * - 로그인 폼을 보여주는 용도
     */
    @GetMapping("login")
    public String shopLogin() {
        return "shop/shop-login"; // 로그인 HTML 이동
    }

    /**
     * 판매자 회원가입 페이지
     * URL: GET /shop/register/start
     *
     * - RegisterRequest DTO를 모델로 넘겨서 화면에서 <form th:object>로 사용
     */
    @GetMapping("register/start")
    public String registerStartForm(Model model) {

        model.addAttribute("registerRequest", new RegisterRequest());
        return "shop/shop-register-start";
    }

    /**
     * 회원 정보 저장 처리
     * URL: POST /shop/register/start
     *
     * 역할:
     * - Valid 검증
     * - memberRole="SHOP" 강제 주입
     * - DB 저장은 하지 않고 세션에 임시 저장
     */
    @PostMapping("register/start")
    public String registerStart(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult,
            HttpSession session) {

        // 1) 유효성 실패 시 다시 폼으로
        if (bindingResult.hasErrors()) {
            return "shop/shop-register-start";
        }

        // 2) 강제 판매자 권한
        dto.setMemberRole("SHOP");

        // 3) DB 저장 X → 세션에 넣어서 사용
        session.setAttribute("tempShopMember", dto);

        return "shop/shop-register-step";
    }

    /**
     * 상점 정보 입력 화면
     * URL: GET /shop/register/step
     *
     * - 세션에 tempShopMember 가 없으면 리다이렉트
     */
    @GetMapping("register/step")
    public String registerStepForm(HttpSession session) {

        RegisterRequest temp = (RegisterRequest) session.getAttribute("tempShopMember");
        if (temp == null) {
            return "redirect:/shop/register/start";
        }

        return "shop/shop-register-step";
    }

    /**
     * 상점 정보 저장 처리
     * URL: POST /shop/register/step
     *
     * 역할:
     * - 저장한 회원정보(tempShopMember) 읽기
     * - memberService.registerShop() 호출 → MemberEntity 생성 + 저장
     * - 사업자등록증 파일 업로드
     * - ShopEntity 조립 후 saveShop()
     * - tempShopMember 삭제
     * - /shop 이동
     */
    @PostMapping("register/step")
    @ResponseBody
    public String registerStep(
            ShopEntity shop,
            HttpSession session,
            @RequestParam(name = "bizFile", required = false) MultipartFile bizFile,
            @RequestParam(name = "longitude") String longitude, @RequestParam(name = "latitude") String latitude)
            throws Exception {

        // 정보 불러오기
        RegisterRequest temp = (RegisterRequest) session.getAttribute("tempShopMember");
        if (temp == null) {
            return "NO_SESSION";
        }

        // 회원 저장 (MemberEntity 생성)
        MemberEntity member = memberService.createShopMember(temp);
        System.out.println("저장된 MemberNo =" + member.getMemberNo());

        // 파일 업로드 처리
        String fileName = null;
        if (!bizFile.isEmpty()) {
            fileName = System.currentTimeMillis() + "_" + bizFile.getOriginalFilename();
            String savePath = "C:/upload/shop/" + fileName;

            File folder = new File("C:/upload/shop/");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            bizFile.transferTo(new File(fileName + savePath));
        }

        // member status값 지정
        member.setStatus(MemberApprovalStatus.PENDING); // 승인대기 상태

        // ShopEntity 값 설정
        shop.setMember(member); // FK 연결
        shop.setSellerName(member.getMemberName()); // 대표자명
        shop.setCreatedAt(LocalDateTime.now());
        shop.setBizImg(fileName);

        shop.setStatus(ShopApprovalStatus.APPLY_REVIEW); // 입점검토 상태
        // location저장
        // Point location = geometryFactory
        // .createPoint(new Coordinate(Float.parseFloat(longitude),
        // Float.parseFloat(latitude)));
        // shop.setLocation(location);

        // 저장 전 필드 확인
        System.out.println("Shop 저장 전 확인");
        System.out.println("shop 정보 = " + shop);
        System.out.println("shopName = " + shop.getShopName());
        System.out.println("shopAddress = " + shop.getShopAddress());
        System.out.println("bizNo = " + shop.getBizNo());
        // System.out.println("location = " + shop.getLocation());

        // 저장
        shopService.saveShop(shop);
        ShopEntity saved = shopService.findByMemberNo(member.getMemberNo());
        System.out.println("저장된 Shop No =" + saved.getShopNo());

        // 세션 초기화
        session.removeAttribute("tempShopMember");

        return "OK";
    }

    /**
     * 판매자 회원가입 - 아이디 중복 확인
     * true : 사용 가능
     * false : 이미 존재
     */
    @GetMapping("check-id")
    @ResponseBody
    public boolean checkShopMemberId(@RequestParam("memberId") String memberId) {
        System.out.println("check-id 호출, memberId =" + memberId);
        return memberService.isMemberIdAvailable(memberId);
    }

    /**
     * 판매자 회원가입 - 이메일 중복 확인
     */
    @GetMapping("check-email")
    @ResponseBody
    public boolean checkShopMemberEmail(@RequestParam("memberEmail") String memberEmail) {
        return memberService.isMemberEmailAvailable(memberEmail);
    }

    /**
     * 상점명 중복 체크
     * return true → 사용 가능
     * return false → 이미 존재
     */
    @GetMapping("check-name")
    @ResponseBody
    public boolean checkShopName(@RequestParam("shopName") String shopName) {
        return !shopService.existsByShopName(shopName);
    }

    /**
     * 판매자 메인 페이지
     * URL: GET /shop
     *
     * - Spring Security로 로그인한 사용자 정보를 @AuthenticationPrincipal 로 받음
     * - 로그인한 사용자의 memberNo → 상점 조회
     * - shop-main.html 로 이동
     */
    @GetMapping("")
    public String shopMain(
            @AuthenticationPrincipal CustomSecurityDetail principal,
            Model model) {

        MemberEntity member = principal.getMember(); // 로그인한 회원 정보
        ShopEntity shop = shopService.findByMemberNo(member.getMemberNo());

        model.addAttribute("shop", shop);

        return "shop/shop-main";
    }
}