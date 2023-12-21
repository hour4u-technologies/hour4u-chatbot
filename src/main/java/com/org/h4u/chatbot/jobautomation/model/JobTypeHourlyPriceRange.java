package com.org.h4u.chatbot.jobautomation.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTypeHourlyPriceRange {
	
	private Level level;

    private BigDecimal hourlyMin;

    private BigDecimal hourlyMax;

}
