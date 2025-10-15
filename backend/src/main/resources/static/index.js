import { loadTossPayments, ANONYMOUS } from "https://js.tosspayments.com/tosspayments-sdk/v2";

// ✅ 1. 본인 계정의 '결제위젯'용 클라이언트 키로 교체하세요.
const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";

// ✅ 2. 백엔드 테스트 금액과 맞추기 (roomId: 1, 2박 기준)
const amount = {
    currency: "KRW",
    value: 500_000,
};

const main = async () => {
    const tossPayments = await loadTossPayments(clientKey);
    const widgets = tossPayments.widgets({
        customerKey: ANONYMOUS
    });

    await widgets.setAmount(amount);

    await Promise.all([
        widgets.renderPaymentMethods({
            selector: "#payment-method",
            variantKey: "DEFAULT",
        }),
        widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
    ]);

    const paymentRequestButton = document.getElementById('payment-request-button');

    paymentRequestButton.addEventListener('click', async () => {
        try {
            await widgets.requestPayment({
                orderId: "order_" + new Date().getTime(),
                orderName: "호텔 예약 테스트",
                // ✅ 3. 성공 시 이동할 페이지 주소를 success.html로 변경
                successUrl: window.location.origin + "/success.html",
                failUrl: window.location.origin + "/fail.html",
            });
        } catch (err) {
            // TODO: 에러 처리
        }
    });
};

main();