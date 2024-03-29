package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper extends TypeAdapter {
   private final Gson context;
   private final TypeAdapter delegate;
   private final Type type;

   TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter delegate, Type type) {
      this.context = context;
      this.delegate = delegate;
      this.type = type;
   }

   public Object read(JsonReader in) throws IOException {
      return this.delegate.read(in);
   }

   public void write(JsonWriter out, Object value) throws IOException {
      TypeAdapter chosen = this.delegate;
      Type runtimeType = this.getRuntimeTypeIfMoreSpecific(this.type, value);
      if (runtimeType != this.type) {
         TypeAdapter runtimeTypeAdapter = this.context.getAdapter(TypeToken.get(runtimeType));
         if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
            chosen = runtimeTypeAdapter;
         } else if (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
            chosen = this.delegate;
         } else {
            chosen = runtimeTypeAdapter;
         }
      }

      chosen.write(out, value);
   }

   private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
      if (value != null && (type == Object.class || type instanceof TypeVariable || type instanceof Class)) {
         type = value.getClass();
      }

      return type;
   }
}
