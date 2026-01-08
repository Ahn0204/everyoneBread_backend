package com.eob.rider.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eob.common.util.FileUtil;
import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.repository.MemberRepository;
import com.eob.order.model.data.OrderDetailEntity;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.order.model.repository.OrderDetailRepository;
import com.eob.order.model.repository.OrderHistoryRepository;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.MemberRegisterForm;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.data.RiderEntity.RiderEntityBuilder;
import com.eob.rider.model.data.RiderRegisterForm;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.repository.ProductRepository;

import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final RiderRepository riderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void registerMember(MemberRegisterForm memberForm, RiderRegisterForm riderForm) {
        MultipartFile file = riderForm.getLicenseFile();
        // 라이더 운전면허증 파일 저장
        String saveFileName = FileUtil.uploadImage(file, "rider/licenseFile");

        MemberEntity member = new MemberEntity();
        member.setMemberId(memberForm.getMemberId());
        member.setMemberPw(passwordEncoder.encode(memberForm.getMemberPw()));
        member.setMemberName(memberForm.getMemberName());
        member.setMemberJumin(memberForm.getMemberJuminFront() + "-" + memberForm.getMemberJuminBack());
        member.setMemberPhone(memberForm.getMemberPhone().replace("-", ""));
        member.setMemberEmail(memberForm.getMemberEmail());
        member.setMemberAddress(memberForm.getRoadAddress() + memberForm.getDetailAddress());
        member.setMemberRole(MemberRoleStatus.RIDER);
        member.setStatus(MemberApprovalStatus.PENDING);
        member.setCreatedAt(LocalDateTime.now());
        this.memberRepository.save(member);

        RiderEntity rider = RiderEntity.builder().member(member).aStatus(ApprovalStatus.PENDING)
                .riderLicense(riderForm.getDriverLicense()).licenseCreatedAt(riderForm.getLicenseCreatedAt())
                .licenseFile(saveFileName).accountName(riderForm.getAccountName()).accountNo(riderForm.getAccountNo())
                .bankName(riderForm.getBankName())
                .createdAt(LocalDateTime.now()).build();

        this.riderRepository.save(rider);
    }

    public void passwordChange() {
        MemberEntity member = this.memberRepository.findByMemberId("test2");
        member.setMemberPw(passwordEncoder.encode("1234"));
        this.memberRepository.save(member);
    }

    // 아이디 중복확인 AJAX
    public boolean ajaxDuplicationId(String memberId) {
        return this.memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복확인 AJAX
    public boolean ajaxDuplicationEmail(String memberEmail) {
        return this.memberRepository.existsByMemberEmail(memberEmail);
    }

    // 아이디 찾기 AJAX (memberName,memberEmail)
    public HashMap<String, String> ajaxFindById(String memberName, String memberEmail, String memberPhone) {
        Optional<MemberEntity> _member = Optional.empty();
        HashMap<String, String> result = new HashMap<String, String>();
        if (memberEmail != null) {
            _member = this.memberRepository.findByMemberNameAndMemberEmailAndMemberRole(memberName, memberEmail,
                    MemberRoleStatus.RIDER);
        } else if (memberPhone != null) {
            _member = this.memberRepository.findByMemberNameAndMemberPhoneAndMemberRole(memberName, memberPhone,
                    MemberRoleStatus.RIDER);
        }

        if (_member.isEmpty()) {
            result.put("result", "false");
            result.put("value", "일치하는 회원이 없습니다.");
        } else {
            result.put("result", "true");
            result.put("value", _member.get().getMemberId());
        }
        return result;
    }

    public RiderEntity getRider(Long riderNo) {
        RiderEntity rider = this.riderRepository.findByRiderNo(riderNo);
        return rider;

    }

    @Transactional
    public void updateRevisionRequest(RiderRegisterForm riderRegisterForm, Long riderNo) {
        RiderEntity rider = this.getRider(riderNo);
        MultipartFile file = riderRegisterForm.getLicenseFile();
        // FileUtil 규칙에 맞는 folderPath
        String folderPath = "rider/licenseFile";

        // 기존 파일 삭제
        FileUtil.deleteFile(folderPath, rider.getLicenseFile());

        // 새 파일 업로드 (null 또는 empty 검사 생략)
        String saveFileName = FileUtil.uploadImage(file, folderPath);

        // 엔티티 업데이트
        rider.setAStatus(ApprovalStatus.PENDING);
        rider.setCreatedAt(LocalDateTime.now());
        rider.setLicenseCreatedAt(riderRegisterForm.getLicenseCreatedAt());
        rider.setRiderLicense(riderRegisterForm.getDriverLicense());
        rider.setLicenseFile(saveFileName);

        this.riderRepository.save(rider);
    }

    public List<OrderHistoryEntity> getOrderHistory(String type, MemberEntity memberEntity) {

        switch (type) {
            // 모든 주문들
            case "all":
                return this.orderHistoryRepository.findTop10ByStatusInOrderByOrderTimeOrderedAtDesc(
                        Arrays.asList(OrderStatus.REQUEST, OrderStatus.PICKUP, OrderStatus.COMPLETE));
            // 라이더가 수락을 받지 않은 모든 주문
            case "request":
                return this.orderHistoryRepository.findTop10ByStatusOrderByOrderTimeOrderedAtDesc(
                        OrderStatus.REQUEST);
            // 라이더가 수락을 받았거나 배달을 완료한 모든 주문
            case "myOrder":
                List<OrderHistoryEntity> pickups = this.orderHistoryRepository
                        .findByStatusAndRiderOrderByOrderTimeOrderedAtDesc(OrderStatus.PICKUP, memberEntity);
                List<OrderHistoryEntity> completes = this.orderHistoryRepository
                        .findTop10ByStatusAndRiderOrderByOrderTimeOrderedAtDesc(OrderStatus.COMPLETE, memberEntity);
                List<OrderHistoryEntity> result = new ArrayList<>();
                result.addAll(pickups);
                result.addAll(completes);
                return result;
            // 그 외에 모든 주문들 출력
            default:
                return this.orderHistoryRepository.findTop10ByStatusInOrderByOrderTimeOrderedAtDesc(
                        Arrays.asList(OrderStatus.REQUEST, OrderStatus.PICKUP, OrderStatus.COMPLETE));
        }
    }

    // public OrderHistoryEntity ajaxOrderDetail(Long orderNo) {
    // // TODO Auto-generated method stub
    // Optional<OrderHistoryEntity> _dto =
    // orderHistoryRepository.findByOrderNo(orderNo);
    // if (_dto.isEmpty()) {
    // return null;
    // } else {
    // List<OrderDetailEntity> detail =
    // orderDetailRepository.findByOrderNo_OrderNo(_dto.get().getOrderNo());
    // for (int i = 0; i < detail.size(); i++) {
    // ProductEntity product =
    // productRepository.findByProductNo(detail.get(i).getProductNo());
    // detail.get(i).setProduct(product);
    // }
    // _dto.get().setOrderDetail(detail);
    // return _dto.get();
    // }
    // }

    public OrderHistoryEntity ajaxOrderDetail(Long orderNo) {

        Optional<OrderHistoryEntity> _dto = orderHistoryRepository.findByOrderNo(orderNo);

        if (_dto.isEmpty()) {
            return null;
        }

        // List<OrderDetailEntity> detail =
        // orderDetailRepository.findByOrderNo_OrderNo(orderNo);

        // _dto.get().setOrderDetail(detail);
        return _dto.get();
    }

}
