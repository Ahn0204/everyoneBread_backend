// 비즈니스 계정 버튼 요소 (hover 시 드롭다운 열기)
const businessBtn = document.getElementById("businessBtn");
// 실제로 펼쳐지는 드롭다운 박스 요소
const dropdownBox = document.getElementById("dropdownBox");

/**
 * 1. businewssBtn Hover -> 드롭다운 열기
 *    버튼 위에 마우스를 올리면 드롭다운 박스를 보이도록 변경
 */
businessBtn.addEventListener("mouseover", () => {
    dropdownBox.style.display = "block";
});

/**
 * 2. 드롭다운 영역 Hover 유지
 *    드롭다운 위에 마우스를 올리면 드롭다운이 계속 열린 상태를 유지
 */
dropdownBox.addEventListener("mouseover", () => {
    dropdownBox.style.display = "block";
});

/**
 * 3. businewssBtn Hover 해제 -> 드롭다운 닫기
 *    버튼에서 마우스가 벗어나면 일정 시간 후 드롭다운 박스를 숨김
 */
businessBtn.addEventListener("mouseout", () => {
    setTimeout(() => {
        if (!dropdownBox.matches(":hover")) {
            dropdownBox.style.display = "none";
        }
    }, 150);
});

/**
 * 4. 드롭다운 영역 Hover 해제 -> 드롭다운 닫기
 *    드롭다운에서 마우스가 벗어나면 드롭다운 박스를 숨김
 */
dropdownBox.addEventListener("mouseout", () => {
    dropdownBox.style.display = "none";
});