package com.jyr.system.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    private BigDecimal totalSalesToday;
    private BigDecimal totalSalesMonth;
    private Long invoiceCountToday;
    private Long invoiceCountMonth;
    private Long totalProducts;
    private Long lowStockProducts;
    private Long pendingReturns;
    private Long pendingPurchaseOrders;
    private List<TopProductDTO> topProducts;
    private List<SalesByDayDTO> salesByDay;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopProductDTO {
        private String productName;
        private Long totalQuantity;
        private BigDecimal totalRevenue;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SalesByDayDTO {
        private String date;
        private BigDecimal total;
        private Long count;
    }
}
