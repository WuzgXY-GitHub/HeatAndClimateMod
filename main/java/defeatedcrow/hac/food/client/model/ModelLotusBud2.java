package defeatedcrow.hac.food.client.model;

import defeatedcrow.hac.core.client.base.DCTileModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLotusBud2 extends DCTileModelBase {

	public ModelRenderer stem1;
	public ModelRenderer stem2;
	public ModelRenderer stem3;
	public ModelRenderer leaf1;
	public ModelRenderer leaf2;

	public ModelLotusBud2() {

		textureWidth = 64;
		textureHeight = 32;

		stem1 = new ModelRenderer(this, 0, 0);
		stem1.addBox(-4F, 4F, -4F, 1, 4, 1, 0F);
		stem1.setRotationPoint(0F, 0F, 0F);
		stem1.rotateAngleX = 0F;
		stem1.rotateAngleY = 0F;
		stem1.rotateAngleZ = 0F;
		stem1.mirror = false;
		stem2 = new ModelRenderer(this, 5, 0);
		stem2.addBox(-1F, 4F, 3F, 1, 4, 1, 0F);
		stem2.setRotationPoint(0F, 0F, 0F);
		stem2.rotateAngleX = 0F;
		stem2.rotateAngleY = 0F;
		stem2.rotateAngleZ = 0F;
		stem2.mirror = false;
		stem3 = new ModelRenderer(this, 10, 0);
		stem3.addBox(3F, 6F, 0F, 1, 2, 1, 0F);
		stem3.setRotationPoint(0F, 0F, 0F);
		stem3.rotateAngleX = 0F;
		stem3.rotateAngleY = 0F;
		stem3.rotateAngleZ = 0F;
		stem3.mirror = false;
		leaf1 = new ModelRenderer(this, 16, 0);
		leaf1.addBox(-1F, -1F, -2F, 2, 2, 4, 0F);
		leaf1.setRotationPoint(-0.5F, 4F, 3F);
		leaf1.rotateAngleX = 0.3490658F;
		leaf1.rotateAngleY = 0F;
		leaf1.rotateAngleZ = 0F;
		leaf1.mirror = false;
		leaf2 = new ModelRenderer(this, 30, 0);
		leaf2.addBox(-1F, -1F, -0.5F, 2, 2, 2, 0F);
		leaf2.setRotationPoint(-3F, 4F, -4F);
		leaf2.rotateAngleX = 0.3490658F;
		leaf2.rotateAngleY = -0.7853982F;
		leaf2.rotateAngleZ = 0F;
		leaf2.mirror = false;
	}

	@Override
	public void render(float scale) {
		render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		stem1.render(scale);
		stem2.render(scale);
		stem3.render(scale);
		leaf1.render(scale);
		leaf2.render(scale);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
	}

	public void setRotationAngles(int num1, int num2) {}

}
