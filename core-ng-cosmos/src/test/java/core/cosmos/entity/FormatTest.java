package core.cosmos.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.cosmos.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Neal
 */
public class FormatTest {
    @Test
    public void formatDate() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Item item = new Item();
        item.id = "1";
        item.updatedTime = new Date();
        item.localDateTime = LocalDateTime.now();
        mapper.writeValueAsString(item);
    }

    static class Item {
        @Id
        public String id;

        @JsonProperty("updated_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX")
        public Date updatedTime;

        @JsonProperty("updated_time2")

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX")
        public LocalDateTime localDateTime;

    }
}
