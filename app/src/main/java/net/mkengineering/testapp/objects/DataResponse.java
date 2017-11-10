package net.mkengineering.testapp.objects;

import java.util.List;

import lombok.Data;

/**
 * Created by MalteChristjan on 28.09.2017.
 */
@Data
public class DataResponse {

    public Long timestamp;
    public List<ResponseEntity> values;
}
