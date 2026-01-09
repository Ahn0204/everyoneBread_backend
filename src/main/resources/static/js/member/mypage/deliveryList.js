function openAddModal() {
    document.querySelector('.modal-overlay').style.display = 'flex';
}

function closeAddModal() {
    document.querySelector('.modal-overlay').style.display = 'none';
}
// 삭제 시 확인창
function deleteAddress(id) {
    if (confirm("정말 삭제하시겠습니까?")) {
        location.href = "/member/mypage/delivery/delete/" + id;
    }
}