package com.message.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MessageInfo implements Serializable{
    private static final long serialVersionUID = 5633474297897542567L;

    private String payload;
    private String messageId;


}
