package com.jyr.system.service.impl;

import com.jyr.system.dto.response.DashboardResponse;
import com.jyr.system.enums.PurchaseOrderStatus;
import com.jyr.system.enums.ReturnStatus;
import com.jyr.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DocumentRepository documentRepository;
    private final DocumentDetailRepository detailRepository;
    private final ProductRepository productRepository;
    private final ProductReturnRepository returnRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        BigDecimal salesToday = documentRepository.sumSalesByDate(today);
        BigDecimal salesMonth = documentRepository.sumSalesByDateRange(firstDayOfMonth, today);
        Long invoicesToday = documentRepository.countInvoicesByDate(today);
        Long invoicesMonth = documentRepository.countInvoicesByDateRange(firstDayOfMonth, today);
        Long totalProducts = productRepository.countActiveProducts();
        Long lowStockCount = productRepository.countLowStockProducts();
        Long pendingReturns = returnRepository.countByStatus(ReturnStatus.SOLICITADA);
        Long pendingPOs = purchaseOrderRepository.countByStatus(PurchaseOrderStatus.PENDIENTE);

        // Top 10 products this month
        List<Object[]> topProductsRaw = detailRepository.findTopProducts(firstDayOfMonth, today);
        List<DashboardResponse.TopProductDTO> topProducts = new ArrayList<>();
        int limit = Math.min(topProductsRaw.size(), 10);
        for (int i = 0; i < limit; i++) {
            Object[] row = topProductsRaw.get(i);
            topProducts.add(DashboardResponse.TopProductDTO.builder()
                    .productName((String) row[0])
                    .totalQuantity((Long) row[1])
                    .totalRevenue((BigDecimal) row[2])
                    .build());
        }

        // Sales by day (last 30 days)
        List<DashboardResponse.SalesByDayDTO> salesByDay = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal dailySales = documentRepository.sumSalesByDate(date);
            Long dailyCount = documentRepository.countInvoicesByDate(date);
            salesByDay.add(DashboardResponse.SalesByDayDTO.builder()
                    .date(date.toString())
                    .total(dailySales)
                    .count(dailyCount)
                    .build());
        }

        return DashboardResponse.builder()
                .totalSalesToday(salesToday)
                .totalSalesMonth(salesMonth)
                .invoiceCountToday(invoicesToday)
                .invoiceCountMonth(invoicesMonth)
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockCount)
                .pendingReturns(pendingReturns)
                .pendingPurchaseOrders(pendingPOs)
                .topProducts(topProducts)
                .salesByDay(salesByDay)
                .build();
    }
}
