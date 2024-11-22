package com.project.course.subscription.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class INRFormatterSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeString("0.00");
        } else {
            NumberFormat formatter = new DecimalFormat("#,##0.00");
            gen.writeString(formatter.format(value));
        }
    }
}
