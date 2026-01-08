const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

/* =========================
   공통 수정 (상점명/소개글/주소)
========================= */
function toggleEdit(btn, inputId, type) {
    const input = document.getElementById(inputId);

    // 수정 모드
    if (input.readOnly) {
        input.readOnly = false;
        input.focus();
        btn.textContent = '저장';
        return;
    }

    // 저장 모드
    fetch(`/shop/mypage/update/${type}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json', [header]: token},
        body: JSON.stringify({ value: input.value })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('변경되었습니다.');
            input.readOnly = true;
            btn.textContent = '변경';
        } else {
            showErrorAlert(data.message || '변경에 실패했습니다.');
        }
    })
    .catch(() => showErrorAlert('서버 오류로 변경에 실패했습니다.'));
}
/* =========================
   주소 수정 (Kakao Postcode API)
   ========================= */
function openAddress() {
    new daum.Postcode({
        oncomplete: function(data) {

            // 1️. 선택한 주소
            const address = data.address;

            // 2️. 화면에 즉시 반영
            document.getElementById('shopAddress').value = address;

            // 3️. 서버 저장
            fetch('/shop/mypage/update/address', {
                method: 'POST',
                headers: {'Content-Type': 'application/json', [header]: token},
                body: JSON.stringify({
                    value: address
                })
            })
            .then(res => res.json())
            .then(data => {
                if (data.result === 'OK') {
                    showSuccessAlert('주소가 변경되었습니다.');
                } else {
                    showErrorAlert(data.message || '주소 변경 실패');
                }
            })
            .catch(() => showErrorAlert('서버 오류'));
        }
    }).open();
}

/* =========================
   휴무일
========================= */
const selectedDays = new Set();

/* 기존 휴무일 버튼 반영 */
const savedHoliday = document.getElementById('savedHoliday')?.value;

if (savedHoliday) {
    savedHoliday.split(',').forEach(day => {
        selectedDays.add(day);
        const btn = document.querySelector(`.day-btn[data-day="${day}"]`);
        if (btn) btn.classList.add('active');
    });
}

/* 휴무일 버튼 클릭 이벤트 */
document.querySelectorAll('.day-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const day = btn.dataset.day;
        btn.classList.toggle('active');

        if (selectedDays.has(day)) selectedDays.delete(day);
        else selectedDays.add(day);
    });
});

function saveHoliday() {
    fetch('/shop/mypage/update/holiday', {
        method: 'POST',
        headers: {'Content-Type': 'application/json', [header]: token},
        body: JSON.stringify({
            // 아무것도 선택 안 하면 "" 저장
            value: Array.from(selectedDays).join(',')
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert(
                selectedDays.size === 0 ? '휴무일 없음으로 저장되었습니다.' : '휴무일이 저장되었습니다.');
        } else {
            showErrorAlert(data.message || '휴무일 저장에 실패했습니다.');
        }
    })
    .catch(() => showErrorAlert('서버 오류로 휴무일 저장에 실패했습니다.'));
}


/* =========================
   영업시간 수정
========================= */
let timeEditMode = false;

function toggleTimeEdit() {
    const open = document.getElementById('openTime');
    const close = document.getElementById('closeTime');

    // 수정 모드
    if (!timeEditMode) {
        open.disabled = false;
        close.disabled = false;
        timeEditMode = true;
        return;
    }

    // 유효성 검사
    if (!open.value || !close.value) {
        showErrorAlert('영업 시작 시간과 종료 시간을 모두 입력해주세요.');
        return;
    }

    if (open.value >= close.value) {
        showErrorAlert('영업 종료 시간은 시작 시간보다 늦어야 합니다.');
        return;
    }

    // 저장 모드
    fetch('/shop/mypage/update/time', {
        method: 'POST',
        headers: {'Content-Type': 'application/json', [header]: token},
        body: JSON.stringify({
            value: open.value + " ~ " + close.value
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('영업시간이 변경되었습니다.');
            open.disabled = true;
            close.disabled = true;
            timeEditMode = false;
        } else {
            showErrorAlert(data.message || '영업시간 변경에 실패했습니다.');
        }
    })
    .catch(() => showErrorAlert('서버 오류로 영업시간 변경에 실패했습니다.'));
}