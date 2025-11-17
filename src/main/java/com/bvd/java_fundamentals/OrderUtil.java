package com.bvd.java_fundamentals;

import com.bvd.java_fundamentals.model.Order;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/*
 * Implement the methods below so that the requirements are met.
 */
public class OrderUtil {

    private OrderUtil() {
    }

    // retrieve orders from csv lines
    public static List<Order> parseCsvLines(final List<String> lines) {
        // Write your code here and replace the return statement

        List<Order> parsedOrders = new ArrayList<>();
        List<String> unparsedOrders = StoreAnalytics.CSV_ORDER;

        for(String order : unparsedOrders) {
            String[] orderData = order.split(",");

            // Bad line in CSV check (continue with the next iteration)
            if(orderData.length < 2) {
                continue;
            }

            Order currentOrder = new Order();
            currentOrder.setOrderId(orderData[0]);
            currentOrder.setCustomerId(orderData[1]);
            currentOrder.setOrderDate(LocalDate.parse(orderData[2]));
            currentOrder.setProductName(orderData[3]);
            currentOrder.setCategory(orderData[4]);
            currentOrder.setUnitPrice(new BigDecimal(orderData[5]));
            currentOrder.setQuantity(Integer.parseInt(orderData[6]));

            parsedOrders.add(currentOrder);

        }

        return parsedOrders;
    }

    // calculate revenue by day
    // revenue = unitPrice * quantity
    public static Map<LocalDate, BigDecimal> revenueByDay(final List<Order> orders) {

        List<Order> ordersCopy = parseCsvLines(StoreAnalytics.CSV_ORDER);
        Map<LocalDate, BigDecimal> revenueByDay = new HashMap<>();
        for(Order currentOrder : ordersCopy) {
            LocalDate orderDate = currentOrder.getOrderDate();
            BigDecimal unitPrice = currentOrder.getUnitPrice();

            if(revenueByDay.containsKey(orderDate)) {
                BigDecimal newVal = revenueByDay.get(orderDate).add(unitPrice);
                revenueByDay.put(orderDate, newVal);
            } else {
                revenueByDay.put(orderDate, unitPrice);
            }

        }

        return revenueByDay;



    }

    // get top "n" products by revenue
    public static List<Map.Entry<String, BigDecimal>> topProductsByRevenue(final List<Order> orders, final int n) {

        List<Map.Entry<String, BigDecimal>> topProductsByRevenue = new ArrayList<>();

        Map<String, BigDecimal> revenueByProduct = new HashMap<>();
        for(Order currentOrder : orders) {
            String orderProductName = currentOrder.getProductName();
            BigDecimal unitPrice = currentOrder.getUnitPrice();

            if(revenueByProduct.containsKey(orderProductName)) {
                BigDecimal newVal = revenueByProduct.get(orderProductName).add(unitPrice);
                revenueByProduct.put(orderProductName, newVal);
            } else {
                revenueByProduct.put(orderProductName, unitPrice);
            }
        }

        List<Map.Entry<String, BigDecimal>> result = new ArrayList<>();

        List<BigDecimal> topRevenueProducts = new ArrayList<>();
        for(Map.Entry<String, BigDecimal> entry : revenueByProduct.entrySet()) {
            topRevenueProducts.add(entry.getValue());
        }
        Collections.sort(topRevenueProducts);

        List<BigDecimal> trimmedTopRevenueProducts = topRevenueProducts.subList(0, n);
        for(Map.Entry<String, BigDecimal> entry : revenueByProduct.entrySet()) {


            if(trimmedTopRevenueProducts.contains(entry.getValue())) {
                result.add(entry);
            }



        }



        return result;
    }

    // get customers who ordered products from at least "minCategories" different categories
    public static List<String> customersWithCategoryDiversity(final List<Order> orders, final int minCategories) {


        Map<String, List<String>> customersWithCategories = new HashMap<>();

        for(Order currentOrder : orders) {
            String customer = currentOrder.getCustomerId();

            if(customersWithCategories.containsKey(customer)) {
                customersWithCategories.get(customer).add(currentOrder.getCategory());
            } else{
                customersWithCategories.put(customer, new ArrayList<>());
                customersWithCategories.get(customer).add(currentOrder.getCategory());
            }


        }

        List<String> result = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : customersWithCategories.entrySet()) {
            if(entry.getValue().size() >= minCategories) {
                result.add(entry.getKey());
            }
        }
        return result;



    }

    // find the first product containing a given substring (case-insensitive)
    public static Optional<Order> findFirstProductContaining(final List<Order> orders, final String product) {

        List<Order> ordersCopy = parseCsvLines(StoreAnalytics.CSV_ORDER);

        return ordersCopy
                .stream()
                .filter((order -> order.getProductName().contains(product)))
                .findFirst();

    }
}
