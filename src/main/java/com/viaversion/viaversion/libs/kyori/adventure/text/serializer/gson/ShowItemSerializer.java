package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.api.BinaryTagHolder;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import java.lang.reflect.Type;

final class ShowItemSerializer implements JsonDeserializer, JsonSerializer {
   // $FF: renamed from: ID java.lang.String
   static final String field_389 = "id";
   static final String COUNT = "count";
   static final String TAG = "tag";

   public HoverEvent.ShowItem deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      if (!object.has("id")) {
         throw new JsonParseException("Not sure how to deserialize show_item hover event");
      } else {
         Key id = (Key)context.deserialize(object.getAsJsonPrimitive("id"), Key.class);
         int count = 1;
         if (object.has("count")) {
            count = object.get("count").getAsInt();
         }

         BinaryTagHolder nbt = null;
         if (object.has("tag")) {
            JsonElement tag = object.get("tag");
            if (tag.isJsonPrimitive()) {
               nbt = BinaryTagHolder.method_21(tag.getAsString());
            } else if (!tag.isJsonNull()) {
               throw new JsonParseException("Expected tag to be a string");
            }
         }

         return HoverEvent.ShowItem.method_416(id, count, nbt);
      }
   }

   public JsonElement serialize(final HoverEvent.ShowItem src, final Type typeOfSrc, final JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.add("id", context.serialize(src.item()));
      int count = src.count();
      if (count != 1) {
         json.addProperty("count", count);
      }

      BinaryTagHolder nbt = src.nbt();
      if (nbt != null) {
         json.addProperty("tag", nbt.string());
      }

      return json;
   }
}
