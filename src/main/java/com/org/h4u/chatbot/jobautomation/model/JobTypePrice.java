package com.org.h4u.chatbot.jobautomation.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTypePrice {

	private Gender gender;

    private BigDecimal basePrice;

    private List<JobTypeHourlyPriceRange> jobTypeHourlyPriceRange;
}
