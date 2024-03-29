package com.viaversion.viabackwards.api.data;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Map;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BackwardsMappings extends MappingDataBase {
   private final Class vvProtocolClass;
   private Int2ObjectMap backwardsItemMappings;
   private Map backwardsSoundMappings;

   public BackwardsMappings(String oldVersion, String newVersion, @Nullable Class vvProtocolClass) {
      this(oldVersion, newVersion, vvProtocolClass, false);
   }

   public BackwardsMappings(String oldVersion, String newVersion, @Nullable Class vvProtocolClass, boolean hasDiffFile) {
      super(oldVersion, newVersion, hasDiffFile);
      Preconditions.checkArgument(vvProtocolClass == null || !vvProtocolClass.isAssignableFrom(BackwardsProtocol.class));
      this.vvProtocolClass = vvProtocolClass;
      this.loadItems = false;
   }

   protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings) {
      if (diffMappings != null) {
         JsonObject diffItems = diffMappings.getAsJsonObject("items");
         if (diffItems != null) {
            this.backwardsItemMappings = VBMappingDataLoader.loadItemMappings(oldMappings.getAsJsonObject("items"), newMappings.getAsJsonObject("items"), diffItems, this.shouldWarnOnMissing("items"));
         }

         JsonObject diffSounds = diffMappings.getAsJsonObject("sounds");
         if (diffSounds != null) {
            this.backwardsSoundMappings = VBMappingDataLoader.objectToMap(diffSounds);
         }
      }

      if (this.vvProtocolClass != null) {
         this.itemMappings = Via.getManager().getProtocolManager().getProtocol(this.vvProtocolClass).getMappingData().getItemMappings().inverse();
      }

      this.loadVBExtras(oldMappings, newMappings);
   }

   @Nullable
   protected Mappings loadFromArray(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
      if (oldMappings.has(key) && newMappings.has(key)) {
         JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
         return new VBMappings(oldMappings.getAsJsonArray(key), newMappings.getAsJsonArray(key), diff, this.shouldWarnOnMissing(key));
      } else {
         return null;
      }
   }

   @Nullable
   protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
      if (oldMappings.has(key) && newMappings.has(key)) {
         JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
         return new VBMappings(oldMappings.getAsJsonObject(key), newMappings.getAsJsonObject(key), diff, this.shouldWarnOnMissing(key));
      } else {
         return null;
      }
   }

   protected JsonObject loadDiffFile() {
      return VBMappingDataLoader.loadFromDataDir("mapping-" + this.newVersion + "to" + this.oldVersion + ".json");
   }

   protected void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
   }

   protected boolean shouldWarnOnMissing(String key) {
      return !key.equals("blocks") && !key.equals("statistics");
   }

   protected Logger getLogger() {
      return ViaBackwards.getPlatform().getLogger();
   }

   public int getNewItemId(int id) {
      return this.itemMappings.get(id);
   }

   public int getNewBlockId(int id) {
      return this.blockMappings.getNewId(id);
   }

   public int getOldItemId(int id) {
      return this.checkValidity(id, this.itemMappings.inverse().get(id), "item");
   }

   @Nullable
   public MappedItem getMappedItem(int id) {
      return this.backwardsItemMappings != null ? (MappedItem)this.backwardsItemMappings.get(id) : null;
   }

   @Nullable
   public String getMappedNamedSound(String id) {
      if (this.backwardsItemMappings == null) {
         return null;
      } else {
         if (id.indexOf(58) == -1) {
            id = "minecraft:" + id;
         }

         return (String)this.backwardsSoundMappings.get(id);
      }
   }

   @Nullable
   public Int2ObjectMap getBackwardsItemMappings() {
      return this.backwardsItemMappings;
   }

   @Nullable
   public Map getBackwardsSoundMappings() {
      return this.backwardsSoundMappings;
   }
}
