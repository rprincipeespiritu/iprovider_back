package com.incloud.hcp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Configuration
public class JacksonConfig {


        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

            // Agrega el serializador customizado para asegurar los dos decimales y la representación numérica
            SimpleModule module = new SimpleModule();
            module.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, IOException {
                    if (value == null) {
                        gen.writeNull();
                    } else {
                        // Asegura 2 decimales y redondeo
                        BigDecimal twoDecimalValue = value.setScale(2, RoundingMode.HALF_UP);
                        // Escribe el BigDecimal como un número JSON (sin comillas)
                        gen.writeNumber(twoDecimalValue);
                    }
                }
            });
            mapper.registerModule(module);

            return mapper;
        }
}
