package com.timothyblumberg.autodidacticism.learnthings.dirtywork;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserializer for deserializing a String to byte[]. Used by GSON
 */
public class ByteArrayDeserializer implements JsonDeserializer<byte[]> {
    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final String byteString = json.getAsString();
        return byteString.getBytes();
    }
}