    // 탭 전환
    const tabSeller = document.getElementById("tabSeller");
    const tabAdmin = document.getElementById("tabAdmin");

    const sellerQnaList = document.getElementById("sellerQnaList");
    const adminQnaList = document.getElementById("adminQnaList");

    tabSeller.addEventListener("click", () => {
        tabSeller.classList.add("active");
        tabAdmin.classList.remove("active");
        sellerQnaList.style.display = "block";
        adminQnaList.style.display = "none";
    });

    tabAdmin.addEventListener("click", () => {
        tabAdmin.classList.add("active");
        tabSeller.classList.remove("active");
        sellerQnaList.style.display = "none";
        adminQnaList.style.display = "block";
    });