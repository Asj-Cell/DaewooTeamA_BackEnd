package com.example.backend.payment.util;

public class CardIssuerLookupUtil {

    public static String getIssuer(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return "기타";
        }

        String bin = cardNumber.replaceAll("[^0-9]", "").substring(0, 6);

        if (bin.startsWith("4")) return "비자카드";
        if (bin.startsWith("51") || bin.startsWith("52") || bin.startsWith("53") || bin.startsWith("54") || bin.startsWith("55")) return "마스터카드";
        if (bin.startsWith("34") || bin.startsWith("37")) return "아멕스카드";

        // 국내 카드사 BIN (일부 예시)
        if (bin.startsWith("94")) return "BC카드";
        if (bin.startsWith("5")) return "삼성카드"; // 마스터카드와 겹칠 수 있어 순서가 중요
        if (bin.startsWith("36") || bin.startsWith("438") || bin.startsWith("457") || bin.startsWith("50") || bin.startsWith("62")) return "신한카드";
        if (bin.startsWith("4")) return "KB국민카드"; // 비자카드와 겹칠 수 있어 순서가 중요
        if (bin.startsWith("35") || bin.startsWith("60")) return "현대카드";

        return "기타";
    }
}