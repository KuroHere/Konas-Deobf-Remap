package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.ArrayList;
import java.util.List;

public class BasicFenceConnectionHandler extends AbstractFenceConnectionHandler {
   static List init() {
      List actions = new ArrayList();
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:oak_fence"));
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:birch_fence"));
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:jungle_fence"));
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:dark_oak_fence"));
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:acacia_fence"));
      actions.add((new BasicFenceConnectionHandler("fenceConnections")).getInitAction("minecraft:spruce_fence"));
      return actions;
   }

   public BasicFenceConnectionHandler(String blockConnections) {
      super(blockConnections);
   }
}
