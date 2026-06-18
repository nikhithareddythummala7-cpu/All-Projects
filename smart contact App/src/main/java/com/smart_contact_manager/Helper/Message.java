package com.smart_contact_manager.Helper;

import com.smart_contact_manager.Enum.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message {
    private String content;
    private MessageType type = MessageType.primary;
}
