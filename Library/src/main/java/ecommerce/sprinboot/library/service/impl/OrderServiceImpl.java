package ecommerce.sprinboot.library.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ecommerce.sprinboot.library.model.*;
import ecommerce.sprinboot.library.repository.*;
import ecommerce.sprinboot.library.service.OrderService;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Order> listAll() {
        return (List<Order>) orderRepository.findAll();
    }

    @Override
    public Order get(long id) {
        return orderRepository.findById(id);
    }

    @Override
    public void saveOrder(ShoppingCart cart) {
        Order order = new Order();
        order.setOrderStatus("PENDING");
        order.setOrderDate(new Date());
        order.setCustomer(cart.getCustomer());
        order.setTotalPrice(cart.getTotalPrices());
        order.setDiscountPrice(customerService.calculateDiscount(cart.getTotalPrices()));
        order.setPaymentStatus("PENDING");
        order.setPaymentType(null);
        order.setTransitionId(null);

        List<OrderDetail> orderDetailList = new ArrayList<>();

        for (CartItem item : cart.getCartItem()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(item.getProduct().getSalePrice());

            // **Reduce Stock Before Saving Order**
            Product product = item.getProduct();
            if (product.getCurrentQuantity() >= item.getQuantity()) {
                product.setCurrentQuantity(product.getCurrentQuantity() - item.getQuantity());
                productRepository.save(product);  // Update stock in DB
            } else {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            orderDetailRepository.save(orderDetail);
            orderDetailList.add(orderDetail);
            cartItemRepository.delete(item);
        }

        order.setOrderDetailList(orderDetailList);
        cart.setCartItem(new HashSet<>());
        cart.setTotalPrices(0);
        cartRepository.save(cart);
        orderRepository.save(order);
    }

    @Override
    public void acceptOrder(Long id) {
        Order order = orderRepository.getById(id);
        order.setOrderStatus("ACCEPTED");
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.getById(id);
        order.setOrderStatus("CANCELED");

        for (OrderDetail orderDetail : order.getOrderDetailList()) {
            Product product = orderDetail.getProduct();
            product.setCurrentQuantity(product.getCurrentQuantity() + orderDetail.getQuantity());
            productRepository.save(product);
        }

        orderRepository.save(order);
    }

    @Override
    public void updatePayment(Order order) {
        Order newOrder = orderRepository.getById(order.getId());

        if (Objects.equals(order.getPaymentType(), "CASH"))
            newOrder.setTransitionId(order.getOrderStatus());
        else
            newOrder.setTransitionId(order.getTransitionId());

        newOrder.setPaymentType(order.getPaymentType());
        newOrder.setPaymentStatus("SUCCESSFUL");

        orderRepository.save(newOrder);
    }
}
