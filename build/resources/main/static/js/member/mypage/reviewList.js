/**
 * 후기 삭제 요청
 * - 공통 SweetAlert 유틸 사용
 * - 성공 / 실패 케이스 명확히 분기
 * - 서버 에러 메시지까지 최대한 표시
 */
function deleteReview(reviewNo) {

    // 삭제 여부 확인 Alert
    showConfirmAlert(
        '후기 삭제',
        '정말 이 리뷰를 삭제하시겠습니까?\n삭제 후에는 복구할 수 없습니다.',
        function () {

            // 삭제 AJAX 요청
            $.ajax({
                url: "/member/mypage/review/delete",
                type: "POST",
                data: { reviewNo: reviewNo },

                /**
                 * 성공 시
                 * - 서버에서 내려준 메시지 표시
                 * - 확인 누르면 페이지 새로고침
                 */
                success: function (message) {
                    showSuccessAlert(message || '리뷰가 삭제되었습니다.', function () {
                        location.reload();
                    });
                },

                /**
                 * 실패 시
                 * - HTTP 상태 코드 기준으로 분기
                 * - 서버에서 내려준 에러 메시지 최대한 노출
                 */
                error: function (xhr) {

                    let errorMessage = '리뷰 삭제 중 오류가 발생했습니다.';

                    // 1️. 서버에서 문자열 메시지를 내려준 경우
                    if (xhr.responseText) {
                        errorMessage = xhr.responseText;
                    }

                    // 2️. 상태 코드별 추가 설명
                    if (xhr.status === 400) {
                        errorMessage += '\n\n[원인]\n잘못된 요청입니다.';
                    } 
                    else if (xhr.status === 403) {
                        errorMessage += '\n\n[원인]\n본인의 리뷰만 삭제할 수 있습니다.';
                    } 
                    else if (xhr.status === 404) {
                        errorMessage += '\n\n[원인]\n이미 삭제되었거나 존재하지 않는 리뷰입니다.';
                    } 
                    else if (xhr.status === 500) {
                        errorMessage += '\n\n[원인]\n서버 내부 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.';
                    }

                    showErrorAlert(errorMessage);
                }
            });
        }
    );
}