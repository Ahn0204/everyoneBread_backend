package com.eob.member.service;

import com.eob.member.model.data.AddressBookEntity;
import com.eob.member.model.data.AddressStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.dto.AddressRequest;
import com.eob.member.repository.AddressBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressBookService {

    private final AddressBookRepository addressBookRepository;

    /* 배송지 목록 */
    @Transactional(readOnly = true)
    public List<AddressBookEntity> getActiveAddresses(MemberEntity member) {
        return addressBookRepository
            .findByMemberNoAndStatus(member.getMemberNo(), AddressStatus.ACTIVE);
    }

    /* 배송지 추가 */
    public void create(MemberEntity member,
                       AddressRequest request) {

        // 기본 배송지면 기존 기본 해제
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressBookRepository.clearDefaultByMember(member.getMemberNo());
        }

        AddressBookEntity address = new AddressBookEntity();
        address.setMemberNo(member.getMemberNo());
        address.setAlias(request.getAlias());
        address.setAddress(request.getAddress());
        address.setIsDefault(
            Boolean.TRUE.equals(request.getIsDefault())
        );
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setStatus(AddressStatus.ACTIVE);

        addressBookRepository.save(address);
    }

    /* 배송지 삭제 (논리 삭제) */
    public void delete(Long addressNo) {

        AddressBookEntity address =
            addressBookRepository.findById(addressNo)
                .orElseThrow(() ->
                    new IllegalArgumentException("배송지 없음"));

        address.setStatus(AddressStatus.DELETED);
    }
}
