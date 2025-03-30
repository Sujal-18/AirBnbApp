package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.entity.Booking;

public interface CheckOutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
