// success.js

const urlParams = new URLSearchParams(window.location.search);
const paymentKey = urlParams.get("paymentKey");
const orderId = urlParams.get("orderId");
const amount = urlParams.get("amount");

// 화면에 결제 정보 표시
document.getElementById("paymentKey").textContent = paymentKey;
document.getElementById("orderId").textContent = orderId;
document.getElementById("amount").textContent = `${amount}원`;

// 로딩 화면과 성공 화면 DOM 요소
const confirmLoadingSection = document.querySelector('.confirm-loading');
const confirmSuccessSection = document.querySelector('.confirm-success');

// 백엔드에 최종 결제 승인을 요청하는 함수
async function confirmPayment() {
    // 백엔드의 FinalPaymentRequestDto 형식에 맞게 데이터 구성
    const requestData = {
        paymentKey: paymentKey,
        orderId: orderId,
        amount: amount,

        // 이것또한 하드코딩되어있는 3개의 정보입니다 실제 서비스에서는 이 값들을 이전 페이지에서 넘겨받아야 합니다.
        roomId: 1,
        checkInDate: "2025-07-07",
        checkOutDate: "2025-07-09"
    };

    try {
        // ✅ 우리 백엔드 서버의 /api/pay 주소로 요청
        const response = await fetch("/api/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                // JWT 인증이 필요하다면 여기에 Authorization 헤더를 추가해야 합니다. (사실 잘 모릅니당)
                // "Authorization": "Bearer <YOUR_JWT_TOKEN>"
            },
            body: JSON.stringify(requestData),
        });

        if (response.ok) {
            // 성공 시 UI 변경
            confirmLoadingSection.style.display = 'none';
            confirmSuccessSection.style.display = 'flex';
            console.log("최종 승인 성공!");
        } else {
            // 실패 시 에러 처리
            const errorBody = await response.json();
            window.location.href = `/fail.html?message=${errorBody.message || "알 수 없는 오류"}&code=${response.status}`;
        }
    } catch (error) {
        console.error("결제 승인 요청 중 네트워크 오류 발생:", error);
        window.location.href = `/fail.html?message=서버 연결에 실패했습니다.&code=NETWORK_ERROR`;
    }
}

// ✅ success.html의 '결제 승인하기' 버튼에 클릭 이벤트 연결
const confirmPaymentButton = document.getElementById('confirmPaymentButton');
confirmPaymentButton.addEventListener('click', confirmPayment);