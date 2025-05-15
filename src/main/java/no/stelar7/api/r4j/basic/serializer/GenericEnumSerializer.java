package no.stelar7.api.r4j.basic.serializer;

import com.google.gson.*;
import no.stelar7.api.r4j.basic.constants.types.CodedEnum;
import no.stelar7.api.r4j.basic.exceptions.APIEnumNotUpToDateException;

import java.lang.reflect.Type;
import java.util.*;

public class GenericEnumSerializer<T> implements JsonDeserializer<T>
{
    @Override
    public T deserialize(JsonElement json, Type t, JsonDeserializationContext context)
    {
        String          id     = json.getAsString();
        List<CodedEnum> values = Arrays.asList(((Class<CodedEnum>) t).getEnumConstants());
        Optional<Object> retu = values.stream()
                                      .map(v -> v.getFromCode(id))
                                      .filter(Optional::isPresent)
                                      .map(Optional::get)
                                      .findFirst();
        
        return retu.map(o -> (T) o)
            .or(() ->
				values.stream()
				.filter(v -> v.name().equals("UNKNOWN"))
				.map(v->(T) v)
				.findFirst()
			).orElseThrow(() -> new APIEnumNotUpToDateException(t, json));
    }
}
