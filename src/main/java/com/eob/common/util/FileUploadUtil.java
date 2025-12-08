package com.eob.common.util;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    /**
     * 파일 검사 메서드
     * <ul>
     * <li>파일의 존재 여부 검사</li>
     * <li>파일의 MIME 타입 검사</li>
     * <li>파일의 확장자 허용 여부 검사</li>
     * </ul>
     * 
     * @param file : 사용자가 업로드한 파일 객체
     * @throws FileValidationException 파일이 없거나, 이미지가 아니거나, 확장자가 허용되지 않을 경우
     */
    public static void validateImageFile(MultipartFile file) {

        // 1. 파일의 존재 여부 검사
        // - 업로드된 파일이 없으면 커스텀 예외(FileValidationException)를 발생시켜 컨트롤러로 알림
        // - 업로드된 파일이 있는 경우 MIME 타입 검사로 이동
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("파일은 필수입니다.");
        }

        // 2. MIME 타입 검사 (이미지 파일의 경우 image/확장자명)
        // - MIME 타입이 없거나 이미지 파일이 아닌경우 커스텀 예외(FileValidationException)를 발생시켜 컨트롤러로 알림
        // - 이미지 파일이 맞을 경우 확장자 검사로 이동
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileValidationException("이미지 파일만 업로드 가능합니다.");
        }

        // 3. 확장자 검사 (보안상 중요)
        // 파일의 확장자 명을 서버에서 허용할 확장자들로만 검사
        // - 허용한 확장자가 아닌 경우 커스텀 예외(FileValidationException)를 발생시켜 컨트롤러로 알림
        // - 허용한 확장자인 경우 파일명 중복 확인 및 서버 업로드
        String name = file.getOriginalFilename().toLowerCase();
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") ||
                name.endsWith(".webp"))) {
            throw new FileValidationException("허용된 확장자만 업로드 가능합니다.");
        }
        // 3. 확장자 검사 List처리 방법
        // List<String> allowTypes = Arrays.asList("image/jpeg",
        // "image/png","image/gif");

        // if (!allowTypes.contains(contentType)) {
        // throw new FileValidationException("지원하지 않는 이미지 형식입니다. (jpg, png, gif)");
        // }
    }

    /**
     * 파일명 중복 처리 메서드
     * 중복된 파일명이 있을 경우 해당 파일명 뒤에 넘버링을 하여 저장<br>
     * <br>
     * 예시) (기존파일_1, 기존파일_2 ...)
     * 
     * @param saveDir          : 파일의 저장 경로 ("static/upload/" 뒤에 올 저장 폴더 경로)
     * @param originalFileName : 사용자가 업로드한 파일명
     * @return String newName : 중복되지 않은 파일명
     */
    public static String getUniqueFileName(String saveDir, String originalFileName) {
        // 1. 파일명/확장자 분리
        // substring을 사용하여 사용자가 업로드한 파일명의 .을 기준으로 앞은 파일명 뒤는 확장자로 저장
        // 예) 운전면허증.png -> 파일명 : 운전명허증 / 확장자 : .png
        String onlyFilename = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 2. 사용할수없는 특수문자 제거
        // \ , / , : , ? , " , < , > , ` 같은 특수문자를 _로 변환
        String filename = onlyFilename.replaceAll("[^a-zA-Z0-9가-힣 _\\-\\.]", "_");

        // 3. 중복 검사 후 사용할 파일명 변수 생성
        String newName;
        int count = 0;

        // 4. while문을 사용하여 중복되지않은 파일명이 나올때 까지 실행
        while (true) {
            // 4-1 파일명 지정
            // count가 0 일 경우 기존의 파일명 사용
            // 예) 운전면허증.png
            // count가 0이 아닐 경우 기존의 파일명_넘버링.확장자로 이름 변경
            // 예) 운전면허증_1.png
            if (count == 0) {
                newName = filename + extension;
            } else {
                newName = filename + "_" + count + extension;
            }

            // 4-2 경로 + 파일명으로 해당 경로를 가지는 객체 생성
            File file = new File(saveDir + newName);

            // 4-3 파일 존재 여부 확인
            // 파일이 존재 할 경우 무시하고 다시 while문으로 다음 파일명_넘버링 진행
            // 파일이 존재하지 않을 경우 해당 파일명을 확정 짓고 while문 종료
            if (!file.exists()) {
                return newName; // 중복 없음 → 확정
            }
            count++;
        }
    }

    // 2. 이미지 업로드(파일명 중복 처리 + transferTo)
    // 매개변수
    // MultipartFile file : 사용자가 업로드한 파일
    // String FolderPath : 파일의 저장 경로 ("static/upload/" 뒤에 올 저장 폴더 경로)
    // 예) rider 폴더에 licenseFile 폴더안에 파일을 저장해야하는 경우 "rider/liceseFile"
    /**
     * 이미지 파일 업로드 (파일 검사 + 파일명 중복 처리 + 파일업로드)
     * 
     * @param file       : 사용자가 업로드한 파일
     * @param folderPath : 파일의 저장 경로 ("static/upload/" 뒤에 올 저장 폴더 경로)<br>
     *                   <br>
     *                   예) rider 폴더에 licenseFile 폴더안에 파일을 저장해야하는 경우
     *                   "rider/liceseFile"
     * @return String newName : 실제 업로드된 파일명
     */
    public static String uploadImage(MultipartFile file, String folderPath) {

        // 1. 파일 검사
        validateImageFile(file);

        // 2. 저장 경로 지정 (프로젝트 내부 static/upload/.../)
        // WINDOW : \ , LINUX : / 이기때문에 File.separator 쓰면 자동으로 설치환경에 따라 (\,/) 자동으로 사용
        // WINDOW 일 경우 : "src/main/resources/static/upload/rider/licenseFile"
        // LINUX 일 경우 : "src\\main\\recources\\static\\upload\\rider\\licenseFile"
        String saveDir = Paths.get(
                System.getProperty("user.dir"), "src", "main", "resources", "static", "upload", folderPath).toString()
                + File.separator;

        // 3. 해당 경로가 없으면 폴더 생성
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 원본 이름가져오기
        String originalName = file.getOriginalFilename();

        // 5. 중복 파일명 처리 (만들어놓은 메서드 사용)
        String newName = getUniqueFileName(saveDir, originalName);

        // 6. 최종 저장 위치 (파일저장경로 + 중복처리한 파일명)
        File saveFile = new File(saveDir + newName);

        // 7. 파일 저장
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileValidationException("파일 저장 실패");
        }

        // 8. DB에 저장할 업로드된 실제 파일명 반환
        return newName;
    }
}
