package com.eob.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.common.sms.service.SmsService;
import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.service.MemberService;
import com.eob.member.service.MypageService;
import com.eob.member.service.WishlistService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;
    private final SmsService smsService;
    private final MypageService mypageService;
    private final WishlistService wishlistService;

    /*
        로그인 페이지
    */
    @GetMapping("login")
    public String loginPage() {
        return "member/member-login";
    }

    /*
        회원가입 유형 선택
    */
    @GetMapping("select")
    public String selectAccount() {
        return "member/member-select";
    }

    /*
        회원가입 페이지
        ?role=USER 기본
    */
    @GetMapping("register") //예솔: 파라미터 이름을 명시했습니다. name=role
    public String registerPage(@RequestParam(name="role",defaultValue = "USER") String role, Model model) {
        RegisterRequest dto = new RegisterRequest();
        dto.setMemberRole(role);  // 기본 USER
        model.addAttribute("registerRequest", dto);
        return "member/member-register";
    }

    /*
        회원가입 처리 (일반회원)
    */
    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult, HttpSession session) {

        // 기본 유효성 검사
        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // SMS 인증 체크
        if(!smsService.isVerified(session)){
            bindingResult.reject("sms.notVerified","휴대폰 인증을 완료해주세요.");
            return "member/member-register";
        }

        // 회원 역할 강제 USER
        dto.setMemberRole("USER");

        // 일반 회원 가입 처리
        memberService.registerUser(dto, bindingResult, session);

        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // 인증 상태 초기화
        session.removeAttribute("SMS_VERIFIED");

        return "redirect:/member/login";
    }

    /*
        아이디 중복 확인
    */
    @GetMapping("check-id")
    @ResponseBody
    public boolean checkId(@RequestParam("memberId") String memberId){
        return memberService.isMemberIdAvailable(memberId);
    }

    /*
        이메일 중복 확인
    */
    @GetMapping("check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("memberEmail") String memberEmail){
        return memberService.isMemberEmailAvailable(memberEmail);
    }

    /**
     * 마이페이지
     */
    @GetMapping("mypage")
    public String mypage(){
        return "redirect:/member/mypage/orderList";
    }

    /**
     * 마이페이지 - 주문 내역
     */
    @GetMapping("mypage/orderList")
    public String orderList(Model model){
        
        // Security 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 로그인 사용자 정보
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();
        // 회원 번호
        Long memberNo = user.getMember().getMemberNo();
        
        model.addAttribute("menu","orderList");
        model.addAttribute("orders", mypageService.getMyOrders(memberNo));

        return "member/mypage/orderList";
    }

    /**
     * 마이페이지 - 즐겨찾기
     */
    @GetMapping("mypage/wishList")
    public String wishList(Model model) {
        // Security 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 사용자 정보
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();     

        // 회원 번호
        Long memberNo = user.getMember().getMemberNo();           

        // 즐겨찾기 목록 조회
        model.addAttribute("wishlist", wishlistService.getMyActiveWishlist(memberNo));

        // 메뉴 활성화용
        model.addAttribute("menu", "wishlist");

        return "member/mypage/wishList";
    }

    /**
     * 마이페이지 - 후기
     */
    @GetMapping("mypage/reviewList")
    public String reviewList(@RequestParam(defaultValue="0")int page, Model model) {

        // 1️. Spring Security 인증 객체 가져오기
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 2️. 로그인 사용자 정보 꺼내기
        CustomSecurityDetail user =
                (CustomSecurityDetail) authentication.getPrincipal();

        // 3️. 로그인 회원 번호 추출
        Long memberNo = user.getMember().getMemberNo();

        // 페이징 후기 조회
        var reviewPage = mypageService.getMyReviews(memberNo, page);

        // 서비스 호출 → 후기 리스트 조회
        model.addAttribute("reviewList", mypageService.getMyReviews(memberNo) );
        // 페이징 정보
        model.addAttribute("page", reviewPage);
        // 마이페이지 메뉴 활성화용
        model.addAttribute("menu", "reviewList");

        // 6️. 후기 리스트 화면 이동
        return "member/mypage/reviewList";
    }

    /**
     * 마이페이지 - 후기 삭제 (AJAX)
     * 
     * 기능 설명
     * - 로그인한 회원의 후기만 삭제 가능
     * - 실제 삭제가 아닌 status = DELETED 처리 (soft delete)
     */
    @PostMapping("mypage/review/delete")
    @ResponseBody
    public ResponseEntity<String> deleteReview(@RequestParam Long reviewNo) {

        // 1️. Spring Security 인증 객체
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 2️. 로그인 사용자 정보
        CustomSecurityDetail user =
                (CustomSecurityDetail) authentication.getPrincipal();

        // 3️. 로그인 회원 번호
        Long memberNo = user.getMember().getMemberNo();

        // 4️. 서비스 호출 (삭제 처리)
        mypageService.deleteMyReview(reviewNo, memberNo);

        // 성공 응답
        return ResponseEntity.ok("후기가 삭제되었습니다.");
    }

    /**
     * 마이페이지 - 문의
     */
    @GetMapping("mypage/qnaList")
    public String qnaList(Model model) {
        model.addAttribute("menu", "qnaList");
        return "member/mypage/qnaList";
    }

    /**
     * 마이페이지 - 배송지 관리
     */
    @GetMapping("mypage/deliveryList")
    public String delivery(Model model) {
        model.addAttribute("menu", "deliveryList");
        return "member/mypage/deliveryList";
    }

    /**
     * 마이페이지 - 개인정보수정
     */
    @GetMapping("mypage/info")
    public String info(Model model) {
        model.addAttribute("menu", "info");
        return "member/mypage/info";
    }
}
