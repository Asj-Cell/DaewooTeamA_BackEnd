// success.js

// --- ⭐ 1. URL에서 모든 파라미터를 추출합니다. ---
const urlParams = new URLSearchParams(window.location.search);
const paymentKey = urlParams.get("paymentKey");
const orderId = urlParams.get("orderId");
const amount = urlParams.get("amount");
const roomId = urlParams.get("roomId");
const checkInDate = urlParams.get("checkInDate");
const checkOutDate = urlParams.get("checkOutDate");
const couponIdFromUrl = urlParams.get("couponId");

// Display payment information on the page
if (document.getElementById("paymentKey")) document.getElementById("paymentKey").textContent = paymentKey;
if (document.getElementById("orderId")) document.getElementById("orderId").textContent = orderId;
if (document.getElementById("amount")) document.getElementById("amount").textContent = `${Number(amount).toLocaleString()}원`;

// Get DOM elements for loading and success UI
const confirmLoadingSection = document.querySelector('.confirm-loading');
const confirmSuccessSection = document.querySelector('.confirm-success');

// Function to request final payment confirmation from the backend
async function confirmPayment() {

    // --- ⭐ 2. Prepare request data matching the backend's FinalPaymentRequestDto ---
    const requestData = {
        paymentKey: paymentKey,
        orderId: orderId,
        amount: Number(amount),
        roomId: Number(roomId),
        checkInDate: checkInDate,
        checkOutDate: checkOutDate,
        couponId: couponIdFromUrl ? Number(couponIdFromUrl) : null // Include couponId if present
    };

    // --- ⭐ 3. Get the JWT token from localStorage ---
    const authToken = localStorage.getItem("jwt_token");
    if (!authToken) {
        alert("로그인이 필요합니다. 로그인 페이지로 이동합니다."); // Alert user if not logged in
        window.location.href = "/login.html"; // Redirect to login page (adjust path if needed)
        return; // Stop execution
    }

    try {
        // --- ⭐ Make the POST request to the backend confirmation endpoint ---
        const response = await fetch("/api/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                // --- ⭐ 4. Add the Authorization header with the JWT token ---
                "Authorization": `Bearer ${authToken}`
            },
            body: JSON.stringify(requestData),
        });

        if (response.ok) {
            // Success: Update UI
            if (confirmLoadingSection) confirmLoadingSection.style.display = 'none';
            if (confirmSuccessSection) confirmSuccessSection.style.display = 'flex';
            console.log("최종 승인 성공!");
            // Optional: Redirect to a reservation complete page
            // window.location.href = "/reservation-complete.html";
        } else {
            // Failure: Parse error message and redirect to fail page
            let errorBody = { message: "결제 승인 실패 (서버 오류)" }; // Default error message
            try {
                errorBody = await response.json();
            } catch (e) {
                console.error("Failed to parse error response:", e);
            }
            console.error("결제 승인 실패:", errorBody);
            window.location.href = `/fail.html?message=${encodeURIComponent(errorBody.message || "결제 승인 실패")}&code=${response.status}`;
        }
    } catch (error) {
        // Network error: Redirect to fail page
        console.error("결제 승인 요청 중 네트워크 오류 발생:", error);
        window.location.href = `/fail.html?message=서버 연결에 실패했습니다.&code=NETWORK_ERROR`;
    }
}

// Attach event listener to the 'Confirm Payment' button
const confirmPaymentButton = document.getElementById('confirmPaymentButton');
if (confirmPaymentButton) {
    confirmPaymentButton.addEventListener('click', confirmPayment);
} else {
    console.error("Could not find the 'confirmPaymentButton'.");
}