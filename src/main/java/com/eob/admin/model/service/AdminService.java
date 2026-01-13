package com.eob.admin.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.admin.model.data.BanInquiryEntity;
import com.eob.admin.model.data.InquiryEntity;
import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.data.SettleHistoryEntity;
import com.eob.admin.model.repository.BanInquiryRepository;
import com.eob.admin.model.repository.FeeHistoryRepository;
import com.eob.admin.model.repository.InquiryRepository;
import com.eob.admin.model.repository.SettleHistoryRepository;
import com.eob.alert.model.service.AlertService;
import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.repository.MemberRepository;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.repository.OrderHistoryRepository;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.DeliveryFeeEntity;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.repository.DeliveryFeeRepository;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.model.data.ShopFeeEntity;
import com.eob.shop.model.data.ShopFeeStatus;
import com.eob.shop.repository.ShopFeeRepository;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final RiderRepository riderRepository;
    private final ShopRepository shopRepository;

    private final ShopFeeRepository shopFeeRepository;
    private final DeliveryFeeRepository deliveryFeeRepository;
    private final FeeHistoryRepository feeHistoryRpository;

    private final SettleHistoryRepository settleHistoryRepository;
    private final InquiryRepository inquiryRepository;
    private final BanInquiryRepository baninquiryRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final BanInquiryRepository banInquiryRepository;

    private final AlertService alertService;
    private final SimpMessagingTemplate messagingTemplat; // STOMP 메세지 발송 전용 객체

    public boolean insertAdmin(InsertAdminForm form) {

        // 관리자 계정 insert
        try {
            // 새 회원 정보 저장할 엔티티 생성
            MemberEntity member = new MemberEntity();
            member.setMemberId(form.getAdminId());
            member.setMemberPw(passwordEncoder.encode(form.getAdminPassword())); // 암호화
            member.setMemberName(form.getAdminName());
            // 이하 관리자 계정용 정보 대입
            member.setMemberJumin("000000-0000000");
            member.setMemberPhone("010-0000-0000");
            member.setMemberEmail("everyoneBread@gmail.com");
            member.setMemberAddress("서울시 관악구 남부순환로 1820 (봉천동) 6층 6B");
            member.setMemberRole(MemberRoleStatus.ADMIN); // 값이 ADMIN인 enum 대입
            member.setStatus(MemberApprovalStatus.ACTIVE); // 관리자계정은 승인 불필요

            // repository를 불러와서 DB에 insert
            memberRepository.save(member);

            return true;
        } catch (Exception e) { // insert중 문제 발생 시
            return false;
        }

    }

    // 아이디 중복 확인 메서드
    public boolean isMemberIdAvailable(String memberId) {
        // !(memberId가 이미 DB에 있으면 true 없으면 false) => 중복id면 false, 중복이 아니면 true
        return !memberRepository.existsByMemberId(memberId);
    }

    /**
     * 라이더 가입, 입점신청 승인
     */

    public boolean doApproval(String param, long objectNo) {
        try {
            if (param.equals("rider")) {
                // rider승인
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.APPROVED);
                rider.setApprovedAt(LocalDateTime.now());
                this.riderRepository.save(rider);
                // member Status변경
                MemberEntity member = rider.getMember();
                member.setStatus(MemberApprovalStatus.ACTIVE);
                this.memberRepository.save(member);
            } else if (param.equals("shop")) {
                // shop승인
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_APPROVED);
                shop.setApprovedDate(LocalDateTime.now());
                this.shopRepository.save(shop);
                // member Status변경
                MemberEntity member = shop.getMember();
                member.setStatus(MemberApprovalStatus.ACTIVE);
                this.memberRepository.save(member);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 라이더 가입, 입점신청 보완 요청
     */
    public boolean doRevision(String param, long objectNo, String rejectReason) {
        try {
            if (param.equals("rider")) {
                // rider보완 요청
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.REVISION_REQUIRED);
                // 보완 사유 저장
                this.riderRepository.save(rider);
                // 유저에게 메일 보내기
            } else if (param.equals("shop")) {
                // shop보완 요청
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_REJECT);
                // 보완 사유 저장
                this.shopRepository.save(shop);
                // 유저에게 메일 보내기
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 상점 정산 처리
     */
    @Transactional
    public void shopSettlement(MemberEntity admin) {
        // SHOP_FEE에서 상점별 최근 잔액 조회
        Optional<List<ShopFeeEntity>> _shopFee = shopFeeRepository
                .findLatestPerShop();
        if (!_shopFee.isPresent()) {
            // 조회된 상점이 없다면
            System.out.println("정산 예정인 상점이 없습니다.");
            return;
        } else {
            // 조회된 상점이 있다면
            List<ShopFeeEntity> shopFee = _shopFee.get();
            for (ShopFeeEntity s : shopFee) {
                // 최종 잔액이 1만원 이상인 레코드 조회
                if (s.getFeeBalance() >= 10000) {
                    // 해당하는 shopEntity 조회
                    ShopEntity shop = s.getShop();
                    // 해당하는 memberEntity 조회
                    MemberEntity member = s.getMember();
                    // 수수료 계산
                    long currentAmount = s.getFeeBalance(); // 정산 전 금액
                    double feeRatio = feeHistoryRpository.findTopByOrderByCreatedAtDesc().getShopFeeRatio(); // 수수료 비율
                    int feeAmount = (int) (currentAmount * feeRatio);// 수수료
                    long settleAmount = currentAmount - feeAmount;// 정산금

                    // 정산 내역 생성
                    SettleHistoryEntity settle = new SettleHistoryEntity();
                    settle.setMemberNo(member);
                    settle.setRole(MemberRoleStatus.SHOP);
                    settle.setSettleName(shop.getAccountName());
                    settle.setBank(shop.getBankName());
                    settle.setAccount(shop.getAccountNo());
                    settle.setCurrentAmount(currentAmount);
                    settle.setFeeAmount(feeAmount);
                    settle.setSettleAmount(settleAmount);
                    settle.setCreatedAt(LocalDateTime.now());
                    settleHistoryRepository.save(settle);

                    // SHOP_FEE에 정산 후 금액 INSERT
                    ShopFeeEntity newS = new ShopFeeEntity();
                    newS.setShop(shop); // 정산된 s와 같은 shop객체
                    newS.setMember(member); // 정산된 s와 같은 member객체
                    newS.setStatus(ShopFeeStatus.WITHDRAW); // 상태 = "환전"
                    newS.setFeeAmount(settleAmount); // 정산된 금액
                    newS.setFeeBalance((long) 0); // 표시될 잔액
                    newS.setCreatedAt(LocalDateTime.now());
                    shopFeeRepository.save(newS);

                    // 상점에 알림DB추가
                    // (보내는멤버Entity, 받는멤버No, 대분류String, 소분류String)
                    alertService.sendAlert(admin, member.getMemberNo(), "SETTLEMENT", "COMPLETED");
                    // 대상에게 알림(정산받은 사람 memberId, 웹소켓 경로, "전달할 메세지")
                    messagingTemplat.convertAndSendToUser(member.getMemberId(), "/to/settlement",
                            "정산이 완료되었습니다.");
                    // 입금명, 입금계좌, 정산 금액, 정산일시 출력 처리(입금 기능 대안)
                    // System.out.println("정산 완료:" + member.getMemberName() + ", " +
                    // s.getFeeBalance() + "원에서 "
                    // + settleAmount + "정산");
                }
                return;
            }
        }
    }

    /**
     * 라이더 정산 처리
     * => 라이더엔티티에 정산 계좌 추가되면 관련 주석 해제
     */
    @Transactional
    public void riderSettlement() {

        Optional<List<DeliveryFeeEntity>> _deliveryFee = deliveryFeeRepository
                .findLatestPerRider();
        if (!_deliveryFee.isPresent()) {
            // 조회된 라이더가 없다면
            System.out.println("정산 예정인 라이더가 없습니다.");
            return;
        } else {
            // 조회된 라이더가 있다면
            List<DeliveryFeeEntity> deliveryFee = _deliveryFee.get();
            for (DeliveryFeeEntity s : deliveryFee) {
                // 최종 잔액이 1만원 이상인 레코드 조회
                if (s.getFeeBalance() >= 10000) {
                    // 해당하는 memberEntity 조회
                    MemberEntity member = s.getRiderNo();
                    // 해당하는 riderEntity 조회
                    Optional<RiderEntity> _rider = riderRepository.findByMember(member);
                    RiderEntity rider = _rider.get();
                    // 수수료 계산
                    long currentAmount = s.getFeeBalance(); // 정산 전 금액
                    double feeRatio = feeHistoryRpository.findTopByOrderByCreatedAtDesc().getRiderFeeRatio(); // 수수료 비율
                    int feeAmount = (int) (currentAmount * feeRatio);// 수수료
                    long settleAmount = currentAmount - feeAmount;// 정산금

                    // 정산 내역 생성
                    SettleHistoryEntity settle = new SettleHistoryEntity();
                    settle.setMemberNo(member);
                    settle.setRole(MemberRoleStatus.RIDER);
                    // settle.setSettleName(rider.getAccountName());
                    // settle.setBank(rider.getBankName());
                    // settle.setAccount(rider.getAccountNo());
                    settle.setCurrentAmount(currentAmount);
                    settle.setFeeAmount(feeAmount);
                    settle.setSettleAmount(settleAmount);
                    settle.setCreatedAt(LocalDateTime.now());
                    settleHistoryRepository.save(settle);

                    // DELIVERY_FEE에 정산 후 금액 INSERT
                    DeliveryFeeEntity newS = new DeliveryFeeEntity();
                    // newS.setShop(shop); // 정산된 s와 같은 shop객체
                    newS.setRiderNo(member); // 정산된 s와 같은 member객체
                    newS.setFeeType(2); // 유형="환전"
                    newS.setFeeAmount((int) settleAmount); // 정산된 금액
                    newS.setFeeBalance(0); // 표시될 잔액
                    newS.setCreatedAt(LocalDateTime.now());
                    deliveryFeeRepository.save(newS);

                    // 대상에게 알림(웹소켓..?)
                    // 입금명, 입금계좌, 정산 금액, 정산일시 출력 처리(입금 기능 대안)
                    System.out.println("정산 완료:" + member.getMemberName() + ", " + s.getFeeBalance() + "원에서 "
                            + settleAmount + "정산");
                }
                return;
            }
        }
    }

    /**
     * 수수료 등록(변경)
     */

    /**
     * 거리별 배송료 등록
     */

    /**
     * 카테고리 변경
     */

    /**
     * 일반 문의 답변 작성 처리
     */
    @Transactional
    public boolean updateAnswer(MemberEntity admin, long inquiryNo, String answer) {
        try {
            // 문의 조회
            Optional<InquiryEntity> _i = inquiryRepository.findById(inquiryNo);
            InquiryEntity i = _i.get();
            // 답변 update
            i.setAnswer(answer); // 답변
            i.setAnsweredAt(LocalDateTime.now()); // 답변 작성일시
            i.setStatus("y");
            inquiryRepository.save(i);

            // 작성자에 알림DB추가
            MemberEntity writer = i.getMember();
            // (보내는멤버Entity, 받는멤버No, 대분류String, 소분류String)
            alertService.sendAlert(admin, writer.getMemberNo(), "INQUIRY", "ANSWERED");

            // 작성자에 웹소켓 알림 전송
            messagingTemplat.convertAndSendToUser(writer.getMemberId(), "/to/inquiry", "문의에 답변이 작성되었습니다.");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 일반 문의 작성 처리
     */
    public boolean insertInquiry(long memberNo, String question) {
        try {
            // member엔티티 조회
            MemberEntity member = memberRepository.findByMemberNo(memberNo);
            // 문의 객체 생성
            InquiryEntity i = new InquiryEntity();
            i.setMember(member); // 작성자 member 엔티티
            i.setRole(member.getMemberRole()); // 작성자 role
            i.setQuestion(question); // 문의
            i.setCreatedAt(LocalDateTime.now()); // 문의 작성일시
            inquiryRepository.save(i);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 주문 문의 작성 처리
     */
    public boolean insertBanInquiry(long memberNo, long orderNo, String question) {
        try {
            // member엔티티 조회
            MemberEntity member = memberRepository.findByMemberNo(memberNo);
            // orderNo엔티티 조회
            Optional<OrderHistoryEntity> _order = orderHistoryRepository.findById(orderNo);
            if (!_order.isPresent()) {
                return false;
            }
            OrderHistoryEntity order = _order.get();
            // 주문 문의 객체 생성
            BanInquiryEntity i = new BanInquiryEntity();
            i.setMember(member); // 작성자 member 엔티티
            i.setRole(member.getMemberRole()); // 작성자 role
            i.setQuestion(question); // 문의 내용
            i.setOrder(order); // 제재 주문
            // i.setBanMember(order.get); 제재 대상.. 특정하기 번거로워서 주석해놓은거 아님ㅎ
            i.setCreatedAt(LocalDateTime.now()); // 문의 작성일시
            baninquiryRepository.save(i);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 주문 답변 작성 처리
     */
    public boolean UpdateBanInquiryAnswer(long banInquiryNo, String answer) {
        try {
            // 문의 조회
            Optional<BanInquiryEntity> _i = banInquiryRepository.findById(banInquiryNo);
            BanInquiryEntity i = _i.get();
            // 답변 update
            i.setAnswer(answer); // 답변
            i.setAnsweredAt(LocalDateTime.now()); // 답변 작성일시
            i.setStatus("y");
            banInquiryRepository.save(i);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
