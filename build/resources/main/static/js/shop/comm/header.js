// csrf 토큰 자동 추가
$(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    if (!token || !header) {
        console.error("CSRF meta tag missing");
        return;
    }

    // 모든 Ajax 요청에 CSRF 자동 추가
    $(document).ajaxSend(function (e, xhr) {
        xhr.setRequestHeader(header, token);
    });
});


let currentTab = 'all';

$(function () {
    // 최초 알림 개수
    getAlertCount();

    // 알림 버튼 클릭
    $('#alertBtn').on('click', function (e) {
        e.stopPropagation();
        $('#alertPanel').toggle();
        loadAlerts();
    });

    // 닫기 버튼
    $('#alertCloseBtn').on('click', function () {
        $('#alertPanel').hide();
    });

    // 바깥 클릭 시 닫기
    $(document).on('click', function () {
        $('#alertPanel').hide();
    });

    // 패널 내부 클릭 시 닫히지 않게
    $('#alertPanel').on('click', function (e) {
        e.stopPropagation();
    });
});

/* =========================
    탭 클릭
========================= */
$('.alert-tab').on('click', function () {
    $('.alert-tab').removeClass('active');
    $(this).addClass('active');
    currentTab = $(this).data('tab');
    loadAlerts();
});

/* =========================
    알림 목록 불러오기
========================= */
function loadAlerts() {
    $('#alertList').empty();

    $.get('/alert/recent', { tab: currentTab }, function (alerts) {
        if (!alerts || alerts.length === 0) {
            $('#alertList').append('<li class="alert-empty">새 알림이 없습니다.</li>');
            return;
        }

        alerts.forEach(alert => {
            $('#alertList').append(renderAlertItem(alert));
        });
    });
}

/* =========================
    알림 한 건 렌더링
========================= */
function renderAlertItem(alert) {
    const li = $('<li>')
        .addClass('alert-item')
        .attr('data-alert-no', alert.alertNo);

    if (alert.readYn === 'N') li.addClass('unread');

    const row = $('<div>').addClass('alert-row');

    const text = $('<span>')
        .addClass('alert-text')
        .append(
            $('<a>')
                .addClass('alert-link')
                .attr('href', alert.linkUrl || '#')
                .text(alert.content)
        );

    const actions = $('<div>').addClass('alert-actions');

    if (alert.readYn === 'N') {
        actions.append(
            $('<button>')
                .addClass('alert-btn read')
                .text('읽음')
        );
    }

    actions.append(
        $('<button>')
            .addClass('alert-btn delete')
            .text('삭제')
    );

    row.append(text, actions);
    li.append(row);
    return li;
}

/* =========================
    읽음 처리
========================= */
$('#alertPanel').on('click', '.alert-btn.read', function () {
    const item = $(this).closest('.alert-item');
    const alertNo = item.data('alert-no');

    $.post('/alert/readAlert', { alertNo }, function (ok) {
        if (ok) {
            item.removeClass('unread');
            item.find('.read').remove();
            decreaseAlertBadge(); // 뱃지 감소
        }
    });
});

/* =========================
    삭제 처리
========================= */
$('#alertPanel').on('click', '.alert-btn.delete', function () {
    const item = $(this).closest('.alert-item');
    const alertNo = item.data('alert-no');

    $.post('/alert/deleteAlert', { alertNo }, function (ok) {
        if (ok) {
            item.remove();
            decreaseAlertBadge(); // 뱃지 감소
        }
    });
});

/* =========================
    알림 뱃지 감소
========================= */
function decreaseAlertBadge() {
    const $badge = $('.alert-badge-count');
    const current = parseInt($badge.text());

    if (isNaN(current)) return;

    const next = current - 1;

    if (next <= 0) {
        $badge.hide();
        $badge.text('');
    } else if (next < 100) {
        $badge.text(next);
    } else {
        $badge.text('99+');
    }
}

/* =========================
    알림 개수 가져오기
========================= */
function getAlertCount() {
    const $badge = $('.alert-badge-count');

    $.post('/alert/ajaxCount', function (cnt) {
        if (cnt > 0) {
            $badge.text(cnt > 99 ? '99+' : cnt).show();
        } else {
            $badge.hide();
        }
    });
}