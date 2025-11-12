// ✅ 1. 본인 계정의 '결제위젯'용 클라이언트 키로 교체하세요.
const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm"; // Replace with your actual client key

// ⭐ 쿠폰 ID 1번 (5,000원 할인)을 사용한다고 가정합니다.
const couponId = null;
// ⭐ 원래 금액(555,000)에서 5,000원을 뺀 금액을 설정합니다.
const amount = { currency: "KRW", value: 555000 };

// ⭐ 예약 정보 (These should eventually be dynamic)
const roomId = 1;
const checkInDate = "2025-03-07"; // Replace with actual check-in date
const checkOutDate = "2025-03-09"; // Replace with actual check-out date

// ✅ 2. import 대신 TossPayments를 직접 사용합니다.
const tossPayments = TossPayments(clientKey);
const widgets = tossPayments.widgets({
    // customerKey는 비회원일 때 고유하게 식별할 수 있는 값입니다.
    // logged-in user ID or a randomly generated UUID
    customerKey: "some_random_customer_key"
});

// ✅ 3. async/await를 사용하지 않아도 되므로 main 함수를 제거하고 바로 실행합니다.
widgets.setAmount(amount); // ⭐ Set the discounted amount

// 결제 수단 위젯 렌더링
widgets.renderPaymentMethods({
    selector: "#payment-method",
    variantKey: "DEFAULT", // Payment method widget style variant key
});

// 이용약관 위젯 렌더링
widgets.renderAgreement({
    selector: "#agreement",
    variantKey: "AGREEMENT" // Agreement widget style variant key
});

// '결제하기' 버튼 요소 가져오기
const paymentRequestButton = document.getElementById('payment-request-button');

// '결제하기' 버튼 클릭 이벤트 리스너 추가
paymentRequestButton.addEventListener('click', () => {

    // ⭐ successUrl에 쿠폰 ID와 예약 정보를 추가합니다.
    const successUrl = new URL(window.location.origin + "/success.html");
    successUrl.searchParams.append("roomId", roomId);
    successUrl.searchParams.append("checkInDate", checkInDate);
    successUrl.searchParams.append("checkOutDate", checkOutDate);
    if (couponId) { // Add couponId only if it exists
        successUrl.searchParams.append("couponId", couponId);
    }

    // 결제 요청
    widgets.requestPayment({
        orderId: "order_" + new Date().getTime(), // Generate a unique order ID
        orderName: "호텔 예약 (쿠폰 적용)",
        successUrl: successUrl.href, // ⭐ Use the modified URL
        failUrl: window.location.origin + "/fail.html",
        // customerName: "로그인한 사용자 이름", // Optional: Add customer name if available
        // customerEmail: "로그인한 사용자 이메일" // Optional: Add customer email if available
    });
});