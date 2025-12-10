package com.eob.admin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.service.AdminService;
import com.eob.member.model.data.MemberEntity;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin") // http://localhost:8080/admin으로 접속 시 매핑
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;
    public final ShopRepository shopRepository;
    public final RiderRepository riderRepository;

    // 로그인 페이지
    @GetMapping("/login")
    public String getAdminLoginP(HttpSession session, Model model) {

        // 세션 값 확인
        String error = (String) session.getAttribute("loginErrorMessage");

        // 로그인 실패 메세지가 있다면
        if (error != null) {
            // 에러 메시지 띄우기
            model.addAttribute("error", error);
            // 세션 삭제
            session.removeAttribute("loginErrorMessage");
        }

        return "admin/comm/admin-login";
    }

    // 메인 페이지
    @GetMapping("/")
    public String getAdminMain() {
        return "admin/comm/admin-main";
    }

    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    // ============== 정산 /admin/settlement
    // ==================================================

    // 헤더, 사이드바에서 '정산' 항목 클릭 시
    @GetMapping("/settlement")
    public String getSettlementP() {
        // 기본 뷰 = 정산 내역 페이지
        return "redirect:/admin/settlement/settlement-list";
    }

    // 정산내역 페이지
    @GetMapping("/settlement/settlement-list")
    public String getSettlementList() {
        return "admin/settlement/settlement-list";
    }

    // 수수료 변경 페이지
    @GetMapping("/settlement/feeHistory-list")
    public String getFeeHistoryList() {
        return "admin/settlement/feeHistory-list";
    }

    // 배송비 변경 페이지
    @GetMapping("/settlement/distanceFeeHistory-list")
    public String getDistanceFeeHistoryList() {
        return "admin/settlement/distanceFeeHistory-list";
    }

    // ============== 회원 /admin/user
    // ==================================================

    // 헤더, 사이드바에서 'user' 항목 클릭 시
    @GetMapping("/user")
    public String getUserP() {
        return "redirect:/admin/user/shopApproval-list";
    }

    // 입점신청 내역 페이지
    @GetMapping("/user/shopApproval-list")
    public String getShopApprovalList(Model model) {

        // DB에 shop이 있으면
        if (shopRepository.findAll().size() != 0) {
            // shop레코드 status별 조회
            List<ShopEntity> reviewList = shopRepository
                    .findByStatusOrderByCreatedAtDesc(ShopApprovalStatus.APPLY_REVIEW); // 미검토
            List<ShopEntity> rejectedList = shopRepository
                    .findByStatusOrderByCreatedAtDesc(ShopApprovalStatus.APPLY_REJECT); // 반려
            List<ShopEntity> approvedList = shopRepository
                    .findByStatusOrderByCreatedAtDesc(ShopApprovalStatus.APPLY_APPROVED); // 승인완료

            // 뷰에 list전달
            model.addAttribute("shopList", reviewList); // 미검토
            model.addAttribute("rejectedList", rejectedList); // 반려
            model.addAttribute("approvedList", approvedList); // 승인완료
        } else if (shopRepository.findAll().size() == 0) {
            // DB에 shop이 없으면
            model.addAttribute("noShop", "조회된 내역이 없습니다.");

        }
        return "admin/user/shopApproval-list";
    }

    // 폐점신청 내역 페이지
    @GetMapping("/user/shopClose-list")
    public String getShopCloseList() {
        return "admin/user/shopClose-list";
    }

    // 라이더승인 내역 페이지
    @GetMapping("/user/riderApproval-list")
    public String getRiderApprovalList(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 라이더 내역 - 페이징 객체로 리턴
        Page<RiderEntity> riderP = riderRepository.findAll(pageable);

        // 라이더 내역이 존재 하지 않을 때
        if (riderP.getTotalElements() == 0) { // riderP의 요소의 총 갯수가 0이면
            model.addAttribute("noRider", "조회된 내역이 없습니다.");
        } else {
            // 라이더 내역이 존재한다면,
            // riderP의 값을 반복하며 aStatus별로 구분하여 출력될 리스트에 대입
            List<RiderEntity> pendingList = riderP.getContent().stream()
                    .filter(r -> r.getAStatus() == ApprovalStatus.PENDING
                            || r.getAStatus() == ApprovalStatus.UNDER_REVIEW)
                    .toList(); // 미검토
            List<RiderEntity> rejectedList = riderP.getContent().stream()
                    .filter(r -> r.getAStatus() == ApprovalStatus.REVISION_REQUIRED).toList(); // 반려
            List<RiderEntity> approvedList = riderP.getContent().stream()
                    .filter(r -> r.getAStatus() == ApprovalStatus.APPROVED).toList(); // 승인완료

            // // 뷰에 list전달
            model.addAttribute("pendingList", pendingList); // 미검토
            model.addAttribute("rejectedList", rejectedList); // 반려
            model.addAttribute("approvedList", approvedList); // 승인완료

            // 글번호 시작값 계산
            int pendingCount = pendingList.size();
            int rejectedCount = rejectedList.size();
            int approvedCount = approvedList.size();
            int total = pendingCount + rejectedCount + approvedCount;

            int pendingStart = total;
            int rejectedStart = total - pendingCount;
            int approvedStart = total - rejectedCount;

            // 뷰에 글번호 시작값 전달
            model.addAttribute("pendingStart", pendingStart);
            model.addAttribute("rejectedStart", rejectedStart);
            model.addAttribute("approvedStart", approvedStart);

        }
        // 페이징 정보 전달
        model.addAttribute("riderP", riderP);
        return "admin/user/riderApproval-list";
    }

    /**
     * 라이더/입점신청 - 보완 요청
     * 
     * @param param        엔티티구분(rider 또는 shop)
     * @param riderNo      라이더번호
     * @param rejectReason 보완사유
     * @return 알림사항에 띄울 메세지
     */
    @PostMapping("/user/{param}/revision")
    @ResponseBody
    public String ajaxApplyRevision(@PathVariable("param") String param, @RequestParam("objectNo") long objectNo,
            @RequestParam("rejectReason") String rejectReason) {
        try {
            if (param.equals("rider")) {
                // rider보완 요청일 경우
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.REVISION_REQUIRED);
                // 보완 사유 저장
                this.riderRepository.save(rider);
                System.out.println(rider.getAStatus());
            } else if (param.equals("shop")) {
                // shop보완 요청일 경우
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_REJECT);
                // 보완 사유 저장
                this.shopRepository.save(shop);
            }
            return "보완이 요청되었습니다.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 라이더/입점신청 - 승인
     * 
     * @param param   엔티티구분(rider 또는 shop)
     * @param riderNo 라이더번호
     * @return 알림사항에 띄울 메세지
     */
    @PostMapping("/user/{param}/approval")
    @ResponseBody
    public String ajaxApplyApproval(@PathVariable("param") String param, @RequestParam("objectNo") long objectNo) {
        try {
            if (param.equals("rider")) {
                // rider승인일 경우
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.APPROVED);
                this.riderRepository.save(rider);
            } else if (param.equals("shop")) {
                // shop승인일 경우
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_APPROVED);
                this.shopRepository.save(shop);
            }
            return "가입이 승인되었습니다.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 관리자 계정 내역(추가) 페이지
    @GetMapping("/user/admin-list")
    public String getAdminList(Model model) { // 그냥 insertAdminForm만 뷰로 전달해도, 뷰에서 th:object로 사용가능
        // redirect 시에는 flashAttribute에 insertAdminForm이 담아져옴(필드 에러 출력에 필요)

        // 필드 에러로 redirect되지 않은 새 페이지라면
        if (!model.containsAttribute("insertAdminForm")) {
            // insertAdminForm객체 생성
            model.addAttribute("insertAdminForm", new InsertAdminForm());
        }

        return "admin/user/admin-list";
    }

    // 관리자 계정 추가 처리
    @PostMapping("/user/insertAdmin")
    public String insertAdmin(@Valid InsertAdminForm insertAdminForm, BindingResult bindingResult,
            RedirectAttributes rttr) {

        // 입력값 유효성 검사
        // insertAdminForm에 담긴 값에 대한 유효성검사결과를 bindingResult객체로 사용
        if (bindingResult.hasErrors()) { // 오류가 있다면
            // 실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded", false);
            // 입력했던 값
            rttr.addFlashAttribute("insertAdminForm", insertAdminForm);
            // 필드 에러
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.insertAdminForm", bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 아이디 중복 여부 검사
        // !(중복id면false, 중복아니면 true) => 중복id면 if문 실행, 아니면 if문 스킵
        if (!adminService.isMemberIdAvailable(insertAdminForm.getAdminId())) {
            // 에러코드, 메세지
            // bindingResult.rejectValue(필드명, 에러코드, 에러메세지)
            bindingResult.rejectValue("adminId", "duplicateId", "이미 사용 중인 아이디입니다.");
            // 실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded", false);
            // 입력했던 값
            rttr.addFlashAttribute("insertAdminForm", insertAdminForm);
            // 필드 에러
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.insertAdminForm", bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 입력값에 오류가 없다면
        // 계정 추가
        boolean insert = adminService.insertAdmin(insertAdminForm);

        if (insert) {// 성공 시
            log.info("adminName:" + insertAdminForm.getAdminName()); // 터미널에 계정이름 출력
            rttr.addFlashAttribute("isSucceeded", true); // 뷰로 success(ture) 전달
        } else { // 실패시
            rttr.addFlashAttribute("isSucceeded", false);
        }

        return "redirect:/admin/user/admin-list"; // 새 admin-list 페이지가 다시 요청됨(redirect)
    }

    // 회원 내역 페이지
    @GetMapping("/user/user-list")
    public String getUserList() {
        return "admin/user/user-list";
    }

    // ============== 문의 /admin/inquiry
    // ==================================================

    // 헤더, 사이드바에서 'inquiry' 항목 클릭 시
    @GetMapping("/inquiry")
    public String getInquiryP() {
        return "redirect:/admin/inquiry/inquiry-list";
    }

    // 일반문의 내역 페이지
    @GetMapping("/inquiry/inquiry-list")
    public String getInquiryList() {
        return "admin/inquiry/inquiry-list";
    }

    // 신고문의 내역 페이지
    @GetMapping("/inquiry/banInquiry-list")
    public String getBanInquiryList() {
        return "admin/inquiry/banInquiry-list";
    }

    // ============== 홈페이지 관리 /admin/homePage
    // ==================================================

    // 헤더, 사이드바에서 'homePage' 항목 클릭 시
    @GetMapping("/homePage")
    public String getHomePageP() {
        return "redirect:/admin/homePage/notice-list";
    }

    // 공지 내역 페이지
    @GetMapping("/homePage/notice-list")
    public String getNoticeList() {
        return "admin/homePage/notice-list";
    }

    // 내역 페이지
    @GetMapping("/homePage/category-list")
    public String getCategoryP() {
        return "admin/homePage/category-list";
    }
}
