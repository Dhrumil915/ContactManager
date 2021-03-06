package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contact.entites.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long>{

	public MyOrder findByOrderId(String orderId);
}
