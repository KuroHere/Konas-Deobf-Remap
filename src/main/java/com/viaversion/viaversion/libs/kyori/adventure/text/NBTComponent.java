package com.viaversion.viaversion.libs.kyori.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NBTComponent extends BuildableComponent {
   @NotNull
   String nbtPath();

   @Contract(
      pure = true
   )
   @NotNull
   NBTComponent nbtPath(@NotNull final String nbtPath);

   boolean interpret();

   @Contract(
      pure = true
   )
   @NotNull
   NBTComponent interpret(final boolean interpret);

   @Nullable
   Component separator();

   @NotNull
   NBTComponent separator(@Nullable final ComponentLike separator);
}
