package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.chat;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;

public class TranslatableRewriter1_16 extends TranslatableRewriter {
   private static final TranslatableRewriter1_16.ChatColor[] COLORS = new TranslatableRewriter1_16.ChatColor[]{new TranslatableRewriter1_16.ChatColor("black", 0), new TranslatableRewriter1_16.ChatColor("dark_blue", 170), new TranslatableRewriter1_16.ChatColor("dark_green", 43520), new TranslatableRewriter1_16.ChatColor("dark_aqua", 43690), new TranslatableRewriter1_16.ChatColor("dark_red", 11141120), new TranslatableRewriter1_16.ChatColor("dark_purple", 11141290), new TranslatableRewriter1_16.ChatColor("gold", 16755200), new TranslatableRewriter1_16.ChatColor("gray", 11184810), new TranslatableRewriter1_16.ChatColor("dark_gray", 5592405), new TranslatableRewriter1_16.ChatColor("blue", 5592575), new TranslatableRewriter1_16.ChatColor("green", 5635925), new TranslatableRewriter1_16.ChatColor("aqua", 5636095), new TranslatableRewriter1_16.ChatColor("red", 16733525), new TranslatableRewriter1_16.ChatColor("light_purple", 16733695), new TranslatableRewriter1_16.ChatColor("yellow", 16777045), new TranslatableRewriter1_16.ChatColor("white", 16777215)};

   public TranslatableRewriter1_16(BackwardsProtocol protocol) {
      super(protocol);
   }

   public void processText(JsonElement value) {
      super.processText(value);
      if (value.isJsonObject()) {
         JsonObject object = value.getAsJsonObject();
         JsonPrimitive color = object.getAsJsonPrimitive("color");
         if (color != null) {
            String colorName = color.getAsString();
            if (!colorName.isEmpty() && colorName.charAt(0) == '#') {
               int rgb = Integer.parseInt(colorName.substring(1), 16);
               String closestChatColor = this.getClosestChatColor(rgb);
               object.addProperty("color", closestChatColor);
            }
         }

         JsonObject hoverEvent = object.getAsJsonObject("hoverEvent");
         if (hoverEvent != null) {
            try {
               Component component = ChatRewriter.HOVER_GSON_SERIALIZER.deserializeFromTree(object);
               JsonObject processedHoverEvent = ((JsonObject)ChatRewriter.HOVER_GSON_SERIALIZER.serializeToTree(component)).getAsJsonObject("hoverEvent");
               processedHoverEvent.remove("contents");
               object.add("hoverEvent", processedHoverEvent);
            } catch (Exception var7) {
               ViaBackwards.getPlatform().getLogger().severe("Error converting hover event component: " + object);
               var7.printStackTrace();
            }
         }

      }
   }

   private String getClosestChatColor(int rgb) {
      int r = rgb >> 16 & 255;
      int g = rgb >> 8 & 255;
      int b = rgb & 255;
      TranslatableRewriter1_16.ChatColor closest = null;
      int smallestDiff = 0;
      TranslatableRewriter1_16.ChatColor[] var7 = COLORS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         TranslatableRewriter1_16.ChatColor color = var7[var9];
         if (color.rgb == rgb) {
            return color.colorName;
         }

         int rAverage = (color.field_1 + r) / 2;
         int rDiff = color.field_1 - r;
         int gDiff = color.field_2 - g;
         int bDiff = color.field_3 - b;
         int diff = (2 + (rAverage >> 8)) * rDiff * rDiff + 4 * gDiff * gDiff + (2 + (255 - rAverage >> 8)) * bDiff * bDiff;
         if (closest == null || diff < smallestDiff) {
            closest = color;
            smallestDiff = diff;
         }
      }

      return closest.colorName;
   }

   private static final class ChatColor {
      private final String colorName;
      private final int rgb;
      // $FF: renamed from: r int
      private final int field_1;
      // $FF: renamed from: g int
      private final int field_2;
      // $FF: renamed from: b int
      private final int field_3;

      ChatColor(String colorName, int rgb) {
         this.colorName = colorName;
         this.rgb = rgb;
         this.field_1 = rgb >> 16 & 255;
         this.field_2 = rgb >> 8 & 255;
         this.field_3 = rgb & 255;
      }
   }
}
