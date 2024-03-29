package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data;

import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BackwardsMappings extends com.viaversion.viabackwards.api.data.BackwardsMappings {
   private final Map attributeMappings = new HashMap();

   public BackwardsMappings() {
      super("1.16", "1.15", Protocol1_16To1_15_2.class, true);
   }

   protected void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
      Iterator var3 = Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings().entrySet().iterator();

      while(var3.hasNext()) {
         Entry entry = (Entry)var3.next();
         this.attributeMappings.put(entry.getValue(), entry.getKey());
      }

   }

   public Map getAttributeMappings() {
      return this.attributeMappings;
   }
}
