package com.viaversion.viabackwards.protocol.protocol1_13to1_13_1.data;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CommandRewriter1_13_1 extends CommandRewriter {
   public CommandRewriter1_13_1(Protocol protocol) {
      super(protocol);
      this.parserHandlers.put("minecraft:dimension", (wrapper) -> {
         wrapper.write(Type.VAR_INT, 0);
      });
   }

   @Nullable
   protected String handleArgumentType(String argumentType) {
      if (argumentType.equals("minecraft:column_pos")) {
         return "minecraft:vec2";
      } else {
         return argumentType.equals("minecraft:dimension") ? "brigadier:string" : super.handleArgumentType(argumentType);
      }
   }
}
