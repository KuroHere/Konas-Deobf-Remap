package com.viaversion.viaversion.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CommentStore {
   private final Map headers = Maps.newConcurrentMap();
   private final char pathSeperator;
   private final int indents;
   private List mainHeader = Lists.newArrayList();

   public CommentStore(char pathSeperator, int indents) {
      this.pathSeperator = pathSeperator;
      this.indents = indents;
   }

   public void mainHeader(String... header) {
      this.mainHeader = Arrays.asList(header);
   }

   public List mainHeader() {
      return this.mainHeader;
   }

   public void header(String key, String... header) {
      this.headers.put(key, Arrays.asList(header));
   }

   public List header(String key) {
      return (List)this.headers.get(key);
   }

   public void storeComments(InputStream inputStream) throws IOException {
      InputStreamReader reader = new InputStreamReader(inputStream);

      String contents;
      try {
         contents = CharStreams.toString(reader);
      } finally {
         reader.close();
      }

      StringBuilder memoryData = new StringBuilder();
      String pathSeparator = Character.toString(this.pathSeperator);
      int currentIndents = 0;
      String key = "";
      ArrayList headers = Lists.newArrayList();
      String[] var9 = contents.split("\n");
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         String line = var9[var11];
         if (!line.isEmpty()) {
            int indent = this.getSuccessiveCharCount(line, ' ');
            String subline = indent > 0 ? line.substring(indent) : line;
            if (subline.startsWith("#")) {
               String txt;
               if (subline.startsWith("#>")) {
                  txt = subline.startsWith("#> ") ? subline.substring(3) : subline.substring(2);
                  this.mainHeader.add(txt);
               } else {
                  txt = subline.startsWith("# ") ? subline.substring(2) : subline.substring(1);
                  headers.add(txt);
               }
            } else {
               int indents = indent / this.indents;
               if (indents <= currentIndents) {
                  String[] array = key.split(Pattern.quote(pathSeparator));
                  int backspace = currentIndents - indents + 1;
                  key = this.join(array, this.pathSeperator, 0, array.length - backspace);
               }

               String separator = key.length() > 0 ? pathSeparator : "";
               String lineKey = line.contains(":") ? line.split(Pattern.quote(":"))[0] : line;
               key = key + separator + lineKey.substring(indent);
               currentIndents = indents;
               memoryData.append(line).append('\n');
               if (!headers.isEmpty()) {
                  this.headers.put(key, headers);
                  headers = Lists.newArrayList();
               }
            }
         }
      }

   }

   public void writeComments(String yaml, File output) throws IOException {
      int indentLength = this.indents;
      String pathSeparator = Character.toString(this.pathSeperator);
      StringBuilder fileData = new StringBuilder();
      int currentIndents = 0;
      String key = "";
      Iterator var8 = this.mainHeader.iterator();

      while(var8.hasNext()) {
         String h = (String)var8.next();
         fileData.append("#> ").append(h).append('\n');
      }

      String[] var26 = yaml.split("\n");
      int var28 = var26.length;

      for(int var10 = 0; var10 < var28; ++var10) {
         String line = var26[var10];
         if (!line.isEmpty()) {
            int indent = this.getSuccessiveCharCount(line, ' ');
            int indents = indent / indentLength;
            String indentText = indent > 0 ? line.substring(0, indent) : "";
            if (indents <= currentIndents) {
               String[] array = key.split(Pattern.quote(pathSeparator));
               int backspace = currentIndents - indents + 1;
               key = this.join(array, this.pathSeperator, 0, array.length - backspace);
            }

            String separator = key.length() > 0 ? pathSeparator : "";
            String lineKey = line.contains(":") ? line.split(Pattern.quote(":"))[0] : line;
            key = key + separator + lineKey.substring(indent);
            currentIndents = indents;
            List header = (List)this.headers.get(key);
            String headerText = header != null ? this.addHeaderTags(header, indentText) : "";
            fileData.append(headerText).append(line).append('\n');
         }
      }

      FileWriter writer = null;

      try {
         writer = new FileWriter(output);
         writer.write(fileData.toString());
         writer.flush();
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (IOException var24) {
            }
         }

      }

   }

   private String addHeaderTags(List header, String indent) {
      StringBuilder builder = new StringBuilder();
      Iterator var4 = header.iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         builder.append(indent).append("# ").append(line).append('\n');
      }

      return builder.toString();
   }

   private String join(String[] array, char joinChar, int start, int length) {
      String[] copy = new String[length - start];
      System.arraycopy(array, start, copy, 0, length - start);
      return Joiner.on(joinChar).join(copy);
   }

   private int getSuccessiveCharCount(String text, char key) {
      int count = 0;

      for(int i = 0; i < text.length() && text.charAt(i) == key; ++i) {
         ++count;
      }

      return count;
   }
}
