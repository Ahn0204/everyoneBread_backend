package com.eob.common.util;

/**
 *
 * 파일 업로드 또는 파일 검증 과정에서
 * 개발자가 직접 정의한 예외를 발생시키기 위해 사용하는 클래스입니다.
 *
 * <ul>
 * <li>파일이 비어 있는 경우</li>
 * <li>파일 확장자가 허용되지 않은 경우</li>
 * <li>파일 크기가 너무 큰 경우</li>
 * <li>파일 저장 중 오류가 발생한 경우</li>
 * </ul>
 *
 * 위와 같은 상황에서 throw new FileValidationException("메시지") 형태로 사용합니다.
 */
public class FileValidationException extends RuntimeException {

    /**
     * 예외 메시지를 부모 클래스(RuntimeException)에 전달하는 생성자
     *
     * @param message : 예외 발생 시 전달할 상세 메시지
     */
    public FileValidationException(String message) {
        super(message);
    }
}
