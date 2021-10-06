package ru.javawebinar.topjava.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import ru.javawebinar.topjava.web.json.JacksonObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeConverterFactory implements ConverterFactory<String, Object> {
    private static final ConverterFactory CONVERTER_FACTORY = new DateTimeConverterFactory();

    public static ConverterFactory getConverterFactory() {
        return CONVERTER_FACTORY;
    }

    @Override
    public <T extends Object> Converter<String, T> getConverter(Class<T> targetType) {
        return new Converter<String, T>() {
            @Override
            public T convert(String source) {
                if (source == null) return null;
                if (LocalDate.class.equals(targetType)) return (T) DateTimeUtil.parseLocalDate(source);
                else return (T) DateTimeUtil.parseLocalTime(source);
            }
        };
    }
}
