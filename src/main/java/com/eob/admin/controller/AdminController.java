package com.eob.admin.controller;

import java.time.LocalDateTime;
import java.util.List;

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

import com.eob.admin.model.data.DistanceFeeForm;
import com.eob.admin.model.data.DistanceFeeHistoryEntity;
import com.eob.admin.model.data.FeeHistoryEntity;
import com.eob.admin.model.data.InquiryEntity;
import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.data.SettleHistoryEntity;
import com.eob.admin.model.repository.DistanceFeeHistoryRepository;
import com.eob.admin.model.repository.FeeHistoryRepository;
import com.eob.admin.model.repository.InquiryRepository;
import com.eob.admin.model.repository.SettleHistoryRepository;
import com.eob.admin.model.service.AdminService;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.rider.model.service.RiderService;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;
import com.eob.shop.service.ShopService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin") // http://localhost:8080/admin으로 접속 시 매핑
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final RiderService riderService;
    private final RiderRepository riderRepository;
    private final MemberRepository memberRepository;
    private final DistanceFeeHistoryRepository distanceFeeHistoryRepository;
    private final FeeHistoryRepository feeHistoryRepository;
    private final SettleHistoryRepository settleHistoryRepository;
    private final InquiryRepository inquiryRepository;

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

    // 관리자 계정 추가(작업용)
    @GetMapping("/register")
    public String registerAdmin(Model model) {
        // 필드 에러로 redirect되지 않은 새 페이지라면
        if (!model.containsAttribute("insertAdminForm")) {
            // insertAdminForm객체 생성
            model.addAttribute("insertAdminForm", new InsertAdminForm());
        }
        return "admin/comm/register";
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
    public String getSettlementList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 정산 내역 - 페이징 객체로 리턴
        Page<SettleHistoryEntity> settleP = settleHistoryRepository.findAll(pageable);

        // 정산 내역이 존재 하지 않을 때
        if (settleP.getTotalElements() == 0) { // settleP 요소의 총 갯수가 0이면
            model.addAttribute("noList", "조회된 내역이 없습니다.");
        } else {
            // Page를 List로 바꾸지 않아 오류가 난다면 다시 변환...
            // // 정산 내역이 존재한다면, adminList 생성
            // List<DistanceFeeHistoryEntity> distanceFeeList = distanceFeeP.getContent();

            // 뷰에 list전달
            model.addAttribute("settleP", settleP);

            // 글번호 시작값 계산
            int start = settleP.getSize();
            // 뷰에 글번호 시작값 전달
            model.addAttribute("start", start);
        }
        // 페이징 정보 전달
        // model.addAttribute("settleP", settleP);
        return "admin/settlement/settlement-list";
    }

    // 수수료 변경 페이지
    @GetMapping("/settlement/feeHistory-list")
    public String getFeeHistoryList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        // 현재 수수료 비율 출력
        // model.addAttribute("shopFeeRatio", shopFeeRatio);

        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 배송비 변경 내역 - 페이징 객체로 리턴
        Page<FeeHistoryEntity> feeP = feeHistoryRepository.findAll(pageable);

        // 배송비 변경 내역이 존재 하지 않을 때
        if (feeP.getTotalElements() == 0) { // distanceFeeP 요소의 총 갯수가 0이면
            model.addAttribute("noUpdate", "조회된 내역이 없습니다.");
        } else {
            // Page를 List로 바꾸지 않아 오류가 난다면 다시 변환...
            // // 배송비 변경 내역이 존재한다면, adminList 생성
            // List<DistanceFeeHistoryEntity> distanceFeeList = distanceFeeP.getContent();

            // 뷰에 list전달
            model.addAttribute("feeP", feeP);

            // 글번호 시작값 계산
            int start = feeP.getSize();
            // 뷰에 글번호 시작값 전달
            model.addAttribute("start", start);
        }
        // 페이징 정보 전달
        model.addAttribute("feeP", feeP);
        return "admin/settlement/feeHistory-list";
    }

    // 수수료 비율 변경 처리
    @PostMapping("/settlement/insertFeeRatio")
    public String insertFeeRatio(@RequestParam(name = "shopFeeRatio") double shopFeeRatio,
            @RequestParam(name = "deliveryFeeRatio") double deliveryFeeRatio,
            @RequestParam(name = "memberNo") int memberNo, RedirectAttributes rttr) {

        try {
            // 수수료 비율 변경 이력 insert
            FeeHistoryEntity feeHistory = new FeeHistoryEntity();
            // 작업자 member객체 꺼내기
            MemberEntity member = memberRepository.findByMemberNo(memberNo);
            feeHistory.setOperation("변경");
            feeHistory.setShopFeeRatio(shopFeeRatio);
            feeHistory.setRiderFeeRatio(deliveryFeeRatio);
            feeHistory.setMemberNo(member);
            feeHistory.setCreatedAt(LocalDateTime.now());
            feeHistoryRepository.save(feeHistory);
            // 성공 여부 alert보내기
            rttr.addFlashAttribute("isSucceeded", true);
        } catch (Exception e) {
            // 실패 여부 alert보내기
            rttr.addFlashAttribute("isSucceeded", false);

        }

        return "redirect:/admin/settlement/feeHistory-list";
    }

    // 배송비 변경 페이지
    @GetMapping("/settlement/distanceFeeHistory-list")
    public String getDistanceFeeHistoryList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {

        // redirect 시에는 flashAttribute에 distanceFeeForm이 담아져옴(필드 에러 출력에 필요) -> 필드별 표시
        // 안해줄거같아서 주석해놓음
        // 필드 에러로 redirect되지 않은 새 페이지라면
        if (!model.containsAttribute("distanceFeeForm")) {
            // distanceFeeForm객체 생성
            model.addAttribute("distanceFeeForm", new DistanceFeeForm());
        }

        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 배송비 변경 내역 - 페이징 객체로 리턴
        Page<DistanceFeeHistoryEntity> distanceFeeP = distanceFeeHistoryRepository.findAll(pageable);

        // 배송비 변경 내역이 존재 하지 않을 때
        if (distanceFeeP.getTotalElements() == 0) { // distanceFeeP 요소의 총 갯수가 0이면
            model.addAttribute("noUpdate", "조회된 내역이 없습니다.");
        } else {
            // Page를 List로 바꾸지 않아 오류가 난다면 다시 변환...
            // // 배송비 변경 내역이 존재한다면, adminList 생성
            // List<DistanceFeeHistoryEntity> distanceFeeList = distanceFeeP.getContent();

            // 뷰에 list전달
            model.addAttribute("distanceFeeList", distanceFeeP);

            // 글번호 시작값 계산
            int start = distanceFeeP.getSize();
            // 뷰에 글번호 시작값 전달
            model.addAttribute("start", start);
        }
        // 페이징 정보 전달
        model.addAttribute("distanceFeeP", distanceFeeP);

        return "admin/settlement/distanceFeeHistory-list";
    }

    // 배송비 생성/변경/삭제
    @PostMapping("/settlement/insertDistanceFee")
    public String crudDistanceFee(@Valid DistanceFeeForm distanceFeeForm, BindingResult bindingResult,
            RedirectAttributes rttr) {

        // 입력값 유효성 검사
        // insertAdminForm에 담긴 값에 대한 유효성검사결과를 bindingResult객체로 사용
        if (bindingResult.hasErrors()) { // 오류가 있다면
            // 실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded", false);
            // 입력했던 값
            rttr.addFlashAttribute("distanceFeeForm", distanceFeeForm);
            // 필드 에러
            // rttr.addFlashAttribute("org.springframework.validation.BindingResult.distanceFeeForm",
            // bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 입력값에 오류가 없다면
        // crud
        // boolean insert = adminService.insertAdmin(distanceFeeForm);

        // if (insert) {// 성공 시
        // log.info("adminName:" + distanceFeeForm.getAdminName()); // 터미널에 계정이름 출력
        // rttr.addFlashAttribute("isSucceeded", true); // 뷰로 success(ture) 전달
        // } else { // 실패시
        // rttr.addFlashAttribute("isSucceeded", false);
        // }

        return "redirect:/admin/user/admin-list"; // 새 admin-list 페이지가 다시 요청됨(redirect)
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
    public String getShopApprovalList(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 입점신청 내역 - 페이징 객체로 리턴
        Page<ShopEntity> shopP = shopRepository.findAll(pageable);

        // 입점 신청 내역이 없으면,
        if (shopP.getTotalElements() == 0) { // shopP의 요소의 총 갯수가 0이면
            model.addAttribute("noShop", "조회된 내역이 없습니다.");
        } else {
            // 입점 신청 내역이 존재한다면,
            // shopP의 값을 반복하며 aStatus별로 구분하여 출력될 리스트에 대입
            List<ShopEntity> pendingList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.APPLY_REVIEW).toList(); // 미검토
            List<ShopEntity> rejectedList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.APPLY_REJECT).toList(); // 반려
            List<ShopEntity> approvedList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.APPLY_APPROVED).toList(); // 승인완료

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

            // 페이징 정보 전달
            model.addAttribute("shopP", shopP);
        }
        return "admin/user/shopApproval-list";
    }

    // 폐점신청 내역 페이지
    @GetMapping("/user/shopClose-list")
    public String getShopCloseList(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // 페이징 설정 객체 초기화
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 폐점신청 내역 - 페이징 객체로 리턴
        Page<ShopEntity> shopP = shopRepository.findByStatusOrderByCreatedAtDesc(pageable);

        // 폐점신청 내역이 없으면,
        if (shopP.getTotalElements() == 0) { // shopP의 요소의 총 갯수가 0이면
            model.addAttribute("noShop", "조회된 내역이 없습니다.");
        } else {
            // 폐점신청 내역이 존재한다면,
            // shopP의 값을 반복하며 Status별로 구분하여 출력될 리스트에 대입
            List<ShopEntity> pendingList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.CLOSE_REVIEW).toList(); // 미검토
            List<ShopEntity> rejectedList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.CLOSE_REJECT).toList(); // 반려
            List<ShopEntity> approvedList = shopP.getContent().stream()
                    .filter(r -> r.getStatus() == ShopApprovalStatus.CLOSE_APPROVED).toList(); // 승인완료

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
        model.addAttribute("shopP", shopP);
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
    public boolean ajaxApplyRevision(@PathVariable("param") String param, @RequestParam("objectNo") long objectNo,
            @RequestParam("rejectReason") String rejectReason) {
        boolean result = false;
        result = adminService.doRevision(param, objectNo, rejectReason);

        return result;
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
    public boolean ajaxApplyApproval(@PathVariable("param") String param, @RequestParam("objectNo") long objectNo) {
        boolean result = false;
        result = adminService.doApproval(param, objectNo);

        return result;
    }

    // 관리자 계정 내역(추가) 페이지
    @GetMapping("/user/admin-list")
    public String getAdminList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        // 그냥 insertAdminForm만 뷰로 전달해도, 뷰에서 th:object로 사용가능
        // redirect 시에는 flashAttribute에 insertAdminForm이 담아져옴(필드 에러 출력에 필요)

        // 필드 에러로 redirect되지 않은 새 페이지라면
        if (!model.containsAttribute("insertAdminForm")) {
            // insertAdminForm객체 생성
            model.addAttribute("insertAdminForm", new InsertAdminForm());
        }

        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 관리자 내역 - 페이징 객체로 리턴
        Page<MemberEntity> adminP = memberRepository.findByMemberRoleAdmin(pageable);

        // 관리자 내역이 존재 하지 않을 때
        if (adminP.getTotalElements() == 0) { // adminP 요소의 총 갯수가 0이면
            model.addAttribute("noAdmin", "조회된 내역이 없습니다.");
        } else {
            // 관리자 내역이 존재한다면, adminList 생성
            List<MemberEntity> adminList = adminP.getContent();

            // 뷰에 list전달
            model.addAttribute("adminList", adminList);

            // 글번호 시작값 계산
            int start = adminList.size();
            // 뷰에 글번호 시작값 전달
            model.addAttribute("start", start);
        }
        // 페이징 정보 전달
        model.addAttribute("adminP", adminP);

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
    public String getInquiryList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        // 페이징 설정 객체 초기화
        // (현재 페이지int, 한 페이지당 보여줄 레코드의 수int, [정렬기준]);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        // 문의 내역 - 페이징 객체로 리턴
        Page<InquiryEntity> inquiryP = inquiryRepository.findAll(pageable);

        // 문의 내역이 존재 하지 않을 때
        if (inquiryP.getTotalElements() == 0) { // inquiryP의 요소의 총 갯수가 0이면
            model.addAttribute("noInquiry", "조회된 내역이 없습니다.");
        } else {
            // 문의 내역이 존재한다면,
            // inquiryP 값을 반복하며 status별로 구분하여 출력될 리스트에 대입
            List<InquiryEntity> pendingList = inquiryP.getContent().stream()
                    .filter(r -> r.getStatus().equals("n"))
                    .toList(); // 미답변
            List<InquiryEntity> answeredList = inquiryP.getContent().stream()
                    .filter(r -> r.getStatus().equals("y")).toList(); // 답변완료

            // // 뷰에 list전달
            model.addAttribute("pendingList", pendingList); // 미검토
            model.addAttribute("answeredList", answeredList); // 답변완료

            // 글번호 시작값 계산
            int pendingCount = pendingList.size();
            int answeredCount = answeredList.size();
            int total = pendingCount + answeredCount;

            int pendingStart = total;
            int answeredStart = total - answeredCount;

            // 뷰에 글번호 시작값 전달
            model.addAttribute("pendingStart", pendingStart);
            model.addAttribute("answeredStart", answeredStart);

        }
        // 페이징 정보 전달
        model.addAttribute("inquiryP", inquiryP);
        return "admin/inquiry/inquiry-list";
    }

    // 일반 문의 답변 완료
    @PostMapping("/inquiry/updateAnswer")
    @ResponseBody
    public boolean ajaxUpdateAnswer(@RequestParam(name = "inquiryNo") long inquiryNo,
            @RequestParam(name = "answer") String answer) {
        boolean result = false;
        result = adminService.updateAnswer(inquiryNo, answer);

        return result;
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
