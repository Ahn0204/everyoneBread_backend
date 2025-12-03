package com.eob.common.util;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public class FileUpload {
    // 1. 파일명 중복 처리 (기존파일_1, 기존파일_2 ...)
    // 받아야 하는 매개변수
    // String saveDir : 파일의 저장 경로 ("static/upload/" 뒤에 올 저장 폴더 경로)
    // String originalFileName : 사용자가 업로드한 파일명
    public static String getUniqueFileName(String saveDir, String originalFileName) {
        // 1-1 파일명/확장자 분리
        // substring을 사용하여 사용자가 업로드한 파일명의 .을 기준으로 앞은 파일명 뒤는 확장자로 저장
        // 예) 운전면허증.png -> 파일명 : 운전명허증 / 확장자 : .png
        String onlyFilename = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 1-2 사용할수없는 특수문자 제거
        // \ , / , : , ? , " , < , > , ` 같은 특수문자를 _로 변환
        String filename = onlyFilename.replaceAll("[^a-zA-Z0-9가-힣 _\\-\\.]", "_");

        // 1-3 중복 검사 후 사용할 파일명 변수 생성
        String newName;
        int count = 0;

        // 1-4 while문을 사용하여 중복되지않은 파일명이 나올때 까지 실행
        while (true) {
            // 1-4-1 파일명 지정
            // count가 0 일 경우 기존의 파일명 사용
            // 예) 운전면허증.png
            // count가 0이 아닐 경우 기존의 파일명_넘버링.확장자로 이름 변경
            // 예) 운전면허증_1.png
            if (count == 0) {
                newName = filename + extension;
            } else {
                newName = filename + "_" + count + extension;
            }

            // 1-4-2 경로 + 파일명으로 해당 경로를 가지는 객체 생성
            File file = new File(saveDir + newName);

            // 1-4-3 파일 존재 여부 확인
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
    public static String uploadImage(MultipartFile file, String folderPath) {

        // 2-1 저장 경로 지정 (프로젝트 내부 static/upload/.../)
        // WINDOW : \ , LINUX : / 이기때문에 File.separator 쓰면 자동으로 설치환경에 따라 (\,/) 자동으로 사용
        // WINDOW 일 경우 : "src/main/resources/static/upload/rider/licenseFile"
        // LINUX 일 경우 : "src\\main\\recources\\static\\upload\\rider\\licenseFile"
        String saveDir = Paths.get(
                System.getProperty("user.dir"), "src", "main", "resources", "static", "upload", folderPath).toString()
                + File.separator;

        // 2-2 해당 경로가 없으면 폴더 생성
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2-3 원본 이름가져오기
        String originalName = file.getOriginalFilename();

        // 2-4 중복 파일명 처리 (만들어놓은 메서드 사용)
        String newName = getUniqueFileName(saveDir, originalName);

        // 2-5 최종 저장 위치 (파일저장경로 + 중복처리한 파일명)
        File saveFile = new File(saveDir + newName);

        // 2-6 저장
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 실패");
        }

        // 2-7 업로드된 실제 파일명 반환
        return newName;
    }
}
