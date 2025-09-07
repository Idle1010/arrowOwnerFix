package com.idle1010.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

	@Redirect(method = "deflect", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setOwner(Lnet/minecraft/entity/Entity;)V"))
	private void redirectSetOwner(ProjectileEntity instance, @Nullable Entity owner) {}

	@Redirect(method="readCustomData", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LazyEntityReference;fromData(Lnet/minecraft/storage/ReadView;Ljava/lang/String;)Lnet/minecraft/entity/LazyEntityReference;"))
	private LazyEntityReference<?> redirectFromData(ReadView view, String key) {
		String strUUID = view.getString(key, "");
		if (strUUID.isEmpty()) {
			return null;
		}
        return new LazyEntityReference<>(UUID.fromString(strUUID));
	}

	@Redirect(method="writeCustomData", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LazyEntityReference;writeData(Lnet/minecraft/entity/LazyEntityReference;Lnet/minecraft/storage/WriteView;Ljava/lang/String;)V"))
	private void redirectWriteData(LazyEntityReference<Entity> reference, WriteView view, String key) {
		if (reference == null) {
			view.putString(key, "");
			return;
		}
		UUID uuid = reference.getUuid();
		if (uuid != null) {
			view.putString(key, uuid.toString());
		} else {
			view.putString(key, "");
		}
	}
}