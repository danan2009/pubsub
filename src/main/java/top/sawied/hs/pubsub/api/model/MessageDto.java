package top.sawied.hs.pubsub.api.model;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class MessageDto {

    @NotEmpty
    private String message;

    private java.util.Date Date = null;

    public MessageDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }
}
