package com.viaversion.viaversion.libs.gson.internal.reflect;

import com.viaversion.viaversion.libs.gson.internal.JavaVersion;
import java.lang.reflect.AccessibleObject;

public abstract class ReflectionAccessor {
   private static final ReflectionAccessor instance = JavaVersion.getMajorJavaVersion() < 9 ? new PreJava9ReflectionAccessor() : new UnsafeReflectionAccessor();

   public abstract void makeAccessible(AccessibleObject var1);

   public static ReflectionAccessor getInstance() {
      return instance;
   }
}
