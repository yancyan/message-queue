package com.message.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PromotionMessage implements Serializable {

    private static final long serialVersionUID = -1748585142041256036L;

    private String desc;
    private Long days;
    private Instant start;
    private Instant end;

    private LocalDateTime sendDate;
}
