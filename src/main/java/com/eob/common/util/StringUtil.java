package com.eob.common.util;

import org.springframework.stereotype.Component;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.rider.model.data.RiderEntity;

// 주소 마스킹을 위한 유틸 클래스
@Component("stringUtil")
public class StringUtil {

    /**
     * 주소를 지정된 형식에 따라 마스킹
     * 
     * @param address
     * @return
     */
    public static String maskAddress(String address) {
        if (address == null)
            return "";
        // 숫자 뒤에 오는 문자열을 마스킹처리
        return address.replaceAll("(\\d+\\s*)(.*)", "$1 ******");
    }

    /**
     * 전화번호를 지정된 형식에 따라 마스킹 처리
     * 
     * @param phone : 전화번호 (예: 01000000000)
     * @return 마스킹 처리된 전화번호 (예: 010-0000-****)
     */
    public static String maskPhone(String phone) {
        // 전화번호가 null이거나 비어있을 경우 빈값 반환
        if (phone == null || phone.isBlank()) {
            return "";
        }
        // number - 0 1 0 0 0 0 0 0 0 0 0
        // index - 0 1 2 3 4 5 6 7 8 9 10

        // 전화번호가 하이픈이 있을 수 있으니
        // 하이픈 제거 (010-1234-5678 → 01012345678)
        phone = phone.replaceAll("-", "");

        int len = phone.length();

        // 10자리 예: 0101234567
        if (len == 10) {
            String first = phone.substring(0, 3); // 010
            String middle = phone.substring(3, 6); // 123
            return first + "-" + middle + "-****";
        }

        // 11자리 예: 01012345678
        if (len == 11) {
            String first = phone.substring(0, 3); // 010
            String middle = phone.substring(3, 7); // 1234
            return first + "-" + middle + "-****";
        }

        return phone;
    }

    /**
     * 운전면허번호를 지정된 형식에 따라 마스킹 처리
     * 
     * @param riderLicense : 회원의
     * @return
     */
    public static String maskLicense(String riderLicense) {
        if (riderLicense == null || riderLicense.length() != 15) {
            return riderLicense; // 형식이 다르면 그대로 반환
        }

        // 가운데 일련번호 6자리 (5~10 index)
        String first = riderLicense.substring(0, 5); // "00-00-"
        String masked = "******"; // 마스킹될 부분
        String last = riderLicense.substring(11); // "-01" 이후

        return first + masked + last;
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        String local = email.split("@")[0];
        String domain = email.split("@")[1];
        String visible;
        String masked;

        if (local.length() < 4) {
            visible = local.substring(0, 2);
        } else {
            visible = local.substring(0, 4);
        }

        // visible 이후를 * 로 대체
        masked = "*".repeat(local.length() - visible.length());
        return visible + masked + "@" + domain;
    }

    // public String maskLicense(String riderLicense) {
    // if (riderLicense == null) {
    // return "";
    // }

    // String sub = riderLicense.substring(6, 12);

    // return riderLicense.replace(sub, "******");
    // }

    /**
     * "01000000000" 형식의 전화번호를 "010-0000-0000" 형식으로 변환
     * 
     * @param phone
     * @return String "***-****-****"
     * 
     */
    public static String formatPhone(String phone) {
        if (phone == null) {
            return "";
        }
        String firstSub = phone.substring(0, 3);
        String secondSub = phone.substring(3, 7);
        String thirdSub = phone.substring(7, 11);
        return firstSub + "-" + secondSub + "-" + thirdSub;
    }

    public MemberEntity maskMemberEntity(MemberEntity member) {
        MemberEntity result = new MemberEntity();

        result.setMemberEmail(this.maskEmail(member.getMemberEmail()));
        result.setMemberPhone(this.maskPhone(member.getMemberPhone()));
        result.setMemberAddress(this.maskAddress(member.getMemberAddress()));
        if (member.getMemberRole() == MemberRoleStatus.RIDER) {
            RiderEntity rider = new RiderEntity();
            rider.setRiderLicense(this.maskLicense(member.getRider().getRiderLicense()));
            result.setRider(rider);
        }
        return result;
    }

}
