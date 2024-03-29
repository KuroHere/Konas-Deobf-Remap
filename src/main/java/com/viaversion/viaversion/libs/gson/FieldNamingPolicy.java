package com.viaversion.viaversion.libs.gson;

import java.lang.reflect.Field;
import java.util.Locale;

public enum FieldNamingPolicy implements FieldNamingStrategy {
   IDENTITY {
      public String translateName(Field f) {
         return f.getName();
      }
   },
   UPPER_CAMEL_CASE {
      public String translateName(Field f) {
         return upperCaseFirstLetter(f.getName());
      }
   },
   UPPER_CAMEL_CASE_WITH_SPACES {
      public String translateName(Field f) {
         return upperCaseFirstLetter(separateCamelCase(f.getName(), " "));
      }
   },
   LOWER_CASE_WITH_UNDERSCORES {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), "_").toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DASHES {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), "-").toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DOTS {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), ".").toLowerCase(Locale.ENGLISH);
      }
   };

   FieldNamingPolicy() {
   }

   static String separateCamelCase(String name, String separator) {
      StringBuilder translation = new StringBuilder();
      int i = 0;

      for(int length = name.length(); i < length; ++i) {
         char character = name.charAt(i);
         if (Character.isUpperCase(character) && translation.length() != 0) {
            translation.append(separator);
         }

         translation.append(character);
      }

      return translation.toString();
   }

   static String upperCaseFirstLetter(String name) {
      int firstLetterIndex = 0;

      for(int limit = name.length() - 1; !Character.isLetter(name.charAt(firstLetterIndex)) && firstLetterIndex < limit; ++firstLetterIndex) {
      }

      char firstLetter = name.charAt(firstLetterIndex);
      if (Character.isUpperCase(firstLetter)) {
         return name;
      } else {
         char uppercased = Character.toUpperCase(firstLetter);
         return firstLetterIndex == 0 ? uppercased + name.substring(1) : name.substring(0, firstLetterIndex) + uppercased + name.substring(firstLetterIndex + 1);
      }
   }

   // $FF: synthetic method
   FieldNamingPolicy(Object x2) {
      this();
   }
}
