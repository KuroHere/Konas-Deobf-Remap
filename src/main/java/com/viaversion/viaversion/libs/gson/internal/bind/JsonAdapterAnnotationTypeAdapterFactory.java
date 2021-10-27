package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.JsonAdapter;
import com.viaversion.viaversion.libs.gson.internal.ConstructorConstructor;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;

   public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
      this.constructorConstructor = constructorConstructor;
   }

   public TypeAdapter create(Gson gson, TypeToken targetType) {
      Class rawType = targetType.getRawType();
      JsonAdapter annotation = (JsonAdapter)rawType.getAnnotation(JsonAdapter.class);
      return annotation == null ? null : this.getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
   }

   TypeAdapter getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken type, JsonAdapter annotation) {
      Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();
      Object typeAdapter;
      if (instance instanceof TypeAdapter) {
         typeAdapter = (TypeAdapter)instance;
      } else if (instance instanceof TypeAdapterFactory) {
         typeAdapter = ((TypeAdapterFactory)instance).create(gson, type);
      } else {
         if (!(instance instanceof JsonSerializer) && !(instance instanceof JsonDeserializer)) {
            throw new IllegalArgumentException("Invalid attempt to bind an instance of " + instance.getClass().getName() + " as a @JsonAdapter for " + type.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
         }

         JsonSerializer serializer = instance instanceof JsonSerializer ? (JsonSerializer)instance : null;
         JsonDeserializer deserializer = instance instanceof JsonDeserializer ? (JsonDeserializer)instance : null;
         typeAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, (TypeAdapterFactory)null);
      }

      if (typeAdapter != null && annotation.nullSafe()) {
         typeAdapter = ((TypeAdapter)typeAdapter).nullSafe();
      }

      return (TypeAdapter)typeAdapter;
   }
}
