package be.cegeka.batchers.taxcalculator.presentation.utils;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.money.Money;

import java.io.IOException;

public class JodaMoneySerializer extends JsonSerializer<Money> {

    @Override
    public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getAmount() + " €");
    }
}
