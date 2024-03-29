package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.NamedSoundMapping;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class SoundPackets1_13 extends RewriterBase {
   private static final String[] SOUND_SOURCES = new String[]{"master", "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice"};

   public SoundPackets1_13(Protocol1_12_2To1_13 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      this.protocol.registerClientbound(ClientboundPackets1_13.NAMED_SOUND, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String newSound = (String)wrapper.get(Type.STRING, 0);
               String oldSound = NamedSoundMapping.getOldId(newSound);
               if (oldSound == null && (oldSound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getMappedNamedSound(newSound)) == null) {
                  if (!Via.getConfig().isSuppressConversionWarnings()) {
                     ViaBackwards.getPlatform().getLogger().warning("Unknown named sound in 1.13->1.12 protocol: " + newSound);
                  }
               } else {
                  wrapper.set(Type.STRING, 0, oldSound);
               }

            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_13.STOP_SOUND, ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
         public void registerMap() {
            this.handler((wrapper) -> {
               wrapper.write(Type.STRING, "MC|StopSound");
               byte flags = (Byte)wrapper.read(Type.BYTE);
               String source;
               if ((flags & 1) != 0) {
                  source = SoundPackets1_13.SOUND_SOURCES[(Integer)wrapper.read(Type.VAR_INT)];
               } else {
                  source = "";
               }

               String sound;
               if ((flags & 2) != 0) {
                  String newSound = (String)wrapper.read(Type.STRING);
                  sound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getMappedNamedSound(newSound);
                  if (sound == null) {
                     sound = "";
                  }
               } else {
                  sound = "";
               }

               wrapper.write(Type.STRING, source);
               wrapper.write(Type.STRING, sound);
            });
         }
      });
      this.protocol.registerClientbound(ClientboundPackets1_13.SOUND, new PacketRemapper() {
         public void registerMap() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int newSound = (Integer)wrapper.get(Type.VAR_INT, 0);
               int oldSound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getSoundMappings().getNewId(newSound);
               if (oldSound == -1) {
                  wrapper.cancel();
               } else {
                  wrapper.set(Type.VAR_INT, 0, oldSound);
               }

            });
         }
      });
   }
}
