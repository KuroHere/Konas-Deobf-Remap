package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.$Gson$Preconditions;
import com.viaversion.viaversion.libs.gson.internal.Streams;
import com.viaversion.viaversion.libs.gson.internal.bind.TreeTypeAdapter.1;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public final class TreeTypeAdapter extends TypeAdapter {
   private final JsonSerializer serializer;
   private final JsonDeserializer deserializer;
   final Gson gson;
   private final TypeToken typeToken;
   private final TypeAdapterFactory skipPast;
   private final TreeTypeAdapter.GsonContextImpl context = new TreeTypeAdapter.GsonContextImpl((1)null);
   private TypeAdapter delegate;

   public TreeTypeAdapter(JsonSerializer serializer, JsonDeserializer deserializer, Gson gson, TypeToken typeToken, TypeAdapterFactory skipPast) {
      this.serializer = serializer;
      this.deserializer = deserializer;
      this.gson = gson;
      this.typeToken = typeToken;
      this.skipPast = skipPast;
   }

   public Object read(JsonReader in) throws IOException {
      if (this.deserializer == null) {
         return this.delegate().read(in);
      } else {
         JsonElement value = Streams.parse(in);
         return value.isJsonNull() ? null : this.deserializer.deserialize(value, this.typeToken.getType(), this.context);
      }
   }

   public void write(JsonWriter out, Object value) throws IOException {
      if (this.serializer == null) {
         this.delegate().write(out, value);
      } else if (value == null) {
         out.nullValue();
      } else {
         JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.context);
         Streams.write(tree, out);
      }
   }

   private TypeAdapter delegate() {
      TypeAdapter d = this.delegate;
      return d != null ? d : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
   }

   public static TypeAdapterFactory newFactory(TypeToken exactType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, false, null);
   }

   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken exactType, Object typeAdapter) {
      boolean matchRawType = exactType.getType() == exactType.getRawType();
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, matchRawType, null);
   }

   public static TypeAdapterFactory newTypeHierarchyFactory(Class hierarchyType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, null, false, hierarchyType);
   }

   private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {
      private GsonContextImpl() {
      }

      public JsonElement serialize(Object src) {
         return TreeTypeAdapter.this.gson.toJsonTree(src);
      }

      public JsonElement serialize(Object src, Type typeOfSrc) {
         return TreeTypeAdapter.this.gson.toJsonTree(src, typeOfSrc);
      }

      public Object deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
         return TreeTypeAdapter.this.gson.fromJson(json, typeOfT);
      }

      // $FF: synthetic method
      GsonContextImpl(1 x1) {
         this();
      }
   }

   private static final class SingleTypeFactory implements TypeAdapterFactory {
      private final TypeToken exactType;
      private final boolean matchRawType;
      private final Class hierarchyType;
      private final JsonSerializer serializer;
      private final JsonDeserializer deserializer;

      SingleTypeFactory(Object typeAdapter, TypeToken exactType, boolean matchRawType, Class hierarchyType) {
         this.serializer = typeAdapter instanceof JsonSerializer ? (JsonSerializer)typeAdapter : null;
         this.deserializer = typeAdapter instanceof JsonDeserializer ? (JsonDeserializer)typeAdapter : null;
         $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
         this.exactType = exactType;
         this.matchRawType = matchRawType;
         this.hierarchyType = hierarchyType;
      }

      public TypeAdapter create(Gson gson, TypeToken type) {
         boolean matches = this.exactType != null ? this.exactType.equals(type) || this.matchRawType && this.exactType.getType() == type.getRawType() : this.hierarchyType.isAssignableFrom(type.getRawType());
         return matches ? new TreeTypeAdapter(this.serializer, this.deserializer, gson, type, this) : null;
      }
   }
}
