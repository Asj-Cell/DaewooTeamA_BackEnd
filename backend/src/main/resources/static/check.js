// ✅ 1. 본인 계정의 '결제위젯'용 클라이언트 키로 교체하세요.
const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const amount = { currency: "KRW", value: 500000 };

// ✅ 2. import 대신 TossPayments를 직접 사용합니다.
const tossPayments = TossPayments(clientKey);
const widgets = tossPayments.widgets({
    customerKey: "some_random_customer_key" // ANONYMOUS 대신 임의의 키 사용
});

// ✅ 3. async/await를 사용하지 않아도 되므로 main 함수를 제거하고 바로 실행합니다.
widgets.setAmount(amount);

widgets.renderPaymentMethods({
    selector: "#payment-method",
    variantKey: "DEFAULT",
});

widgets.renderAgreement({
    selector: "#agreement",
    variantKey: "AGREEMENT"
});

const paymentRequestButton = document.getElementById('payment-request-button');

paymentRequestButton.addEventListener('click', () => {
    widgets.requestPayment({
        orderId: "order_" + new Date().getTime(),
        orderName: "호텔 예약 테스트",
        successUrl: window.location.origin + "/success.html",
        failUrl: window.location.origin + "/fail.html",
    });
});