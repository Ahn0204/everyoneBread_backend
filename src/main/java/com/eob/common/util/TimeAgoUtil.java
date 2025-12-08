package com.eob.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeAgoUtil {

	/**
	 * 게시글 , 주문 목록 출력 시 작성 시간을 상대적 시간으로 변환
	 * 
	 * @param time : LocalDateTime 시간
	 * @return "방금 전", "n초 전", "n분 전", "n시간 전", "n일 전"
	 */
	public static String toRelativeTime(LocalDateTime time) {
		// 현재 시간을 가지고 있는 객체 생성
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		// 입력받은시간과 현재시간 사이의 시간을 계산
		Duration duration = Duration.between(time, now);

		// 계산된 시간을 초단위로 변환
		long seconds = duration.getSeconds();

		// -------- 미래 시간 보정 --------
		// 예: 서버시간 불일치로 time > now가 되는 경우
		if (seconds < 0) {
			seconds = 0;
		}

		// 상대 시간 출력을 위해 초 단위 값을
		// 분 / 시간 / 일 단위로 변환
		long minutes = seconds / 60; // 초(sec) → 분(min) 변환
		long hours = seconds / 3600; // 초(sec) → 시간(hour) 변환
		long days = seconds / 86400; // 초(sec) → 일(day) 변환 (24 * 3600)

		// 10초 미만일 경우
		if (seconds < 10) {
			return "방금 전";
		}
		// 1분 미만일 경우
		if (minutes < 1) {
			return seconds + "초 전";
		}
		// 1시간 미만일 경우
		if (minutes < 60) {
			return minutes + "분 전";
		}
		// 24시간 미만일 경우
		if (hours < 24) {
			return hours + "시간 전";
		}
		// 3일 이하일 경우
		if (days <= 3) {
			return days + "일 전";
		}

		// 그 이상의 경우 시간차의 경우 , 날짜 출력
		return time.toLocalDate().toString();
	}
}
