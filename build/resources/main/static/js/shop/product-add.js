    // 이미지 용량 + 확장자 검사
    document.getElementById("imageInput").addEventListener("change", function (e) {
        let file = e.target.files[0];
        let preview = document.getElementById("previewBox");
        // let fileName = document.getElementById("fileName");
    
        if (!file) return;
    
        // 확장자 검사
        // JPG, PNG 만 허용
        const allowExt = ["jpg", "jpeg", "png"];
        let ext = file.name.split(".").pop().toLowerCase();
        if (!allowExt.includes(ext)) {
            showErrorAlert("JPG, PNG 이미지만 업로드 가능합니다.");
            e.target.value = "";
            return;
        }
    
        // 용량 검사 (5MB 이하)
        if (file.size > 5 * 1024 * 1024) {
            showErrorAlert("이미지 용량은 5MB 이하만 가능합니다.");
            e.target.value = "";
            return;
        }
    
        // 이미지 해상도 검사
        let img = new Image();
        img.onload = function () {
            if (img.width < 600 || img.height < 600) {
                showErrorAlert("이미지는 최소 600×600px 이상이어야 합니다.");
                e.target.value = "";
                return;
            }
        };
        img.src = URL.createObjectURL(file);
    
        // 미리보기
        let reader = new FileReader();
        reader.onload = function (event) {
            preview.innerHTML = `<img src="${event.target.result}" 
                                    style="width:100%; height:100%; object-fit:cover;">`;
        };
        reader.readAsDataURL(file);
    
        // 파일명 표시
        // fileName.textContent = "파일명: " + file.name;
    });
    
    // 삭제 버튼
    document.getElementById("deleteImgBtn").addEventListener("click", function () {
        document.getElementById("imageInput").value = "";
        document.getElementById("previewBox").innerHTML = "기본 이미지";
        document.getElementById("fileName").textContent = "";
    });
    
    // 가격 3자리 콤마 적용
    const priceInput = document.getElementById("priceInput");
    const priceHidden = document.getElementById("priceHidden");
    
    priceInput.addEventListener("input", () => {
        let num = priceInput.value.replace(/[^0-9]/g, "");
        priceHidden.value = num; // 숫자만 hidden으로 저장
        priceInput.value = num.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    });
    
    
    // 등록 버튼 클릭 시
    document.getElementById("submitBtn").addEventListener("click", () => {
        Swal.fire({
            title: "상품을 등록할까요?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "등록",
            cancelButtonText: "취소"
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById("productForm").submit();
            }
        });
    });