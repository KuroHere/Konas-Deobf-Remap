package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.minecraft.BlockFace;
import java.util.HashMap;
import java.util.Map;

public class BlockData {
   private final Map connectData = new HashMap();

   public void put(String key, boolean[] booleans) {
      this.connectData.put(key, booleans);
   }

   public boolean connectsTo(String blockConnection, BlockFace face, boolean pre1_12AbstractFence) {
      boolean[] booleans = null;
      if (pre1_12AbstractFence) {
         booleans = (boolean[])this.connectData.get("allFalseIfStairPre1_12");
      }

      if (booleans == null) {
         booleans = (boolean[])this.connectData.get(blockConnection);
      }

      return booleans != null && booleans[face.ordinal()];
   }
}
